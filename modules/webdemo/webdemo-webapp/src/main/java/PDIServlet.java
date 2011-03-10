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

import java.io.*;

import java.sql.*;

import java.text.*;

import java.util.*;

import javax.naming.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.sql.DataSource;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.Task;
import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Title;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.dao.PIMCalendarDAO;
import com.funambol.foundation.items.dao.PIMContactDAO;
import com.funambol.foundation.items.model.CalendarWrapper;
import com.funambol.foundation.items.model.ContactWrapper;

/**
 * This servlet can be used to view, update and create the contact, the event
 * and the task details.
 *
 * @version $Id: PDIServlet.java,v 1.1 2008-03-29 18:55:09 stefano_fornari Exp $
 */
public class PDIServlet extends HttpServlet {

    // --------------------------------------------------------------- Constants
    public final static String PARAM_DATASOURCE_NAME = "ds-name";

    public final static String TYPE_CONTACT = "contact";
    public final static String TYPE_EVENT   = "event"  ;
    public final static String TYPE_TASK    = "task"   ;

    //from jsp
    public final static String PARAM_OPT  = "operation";
    public final static String PARAM_NEXT = "next"     ;
    public final static String PARAM_ITEM = "item"     ;
    public final static String PARAM_TYPE = "type"     ;

    //
    //from-to jsp for contact
    //
    public final static String PARAM_NAME        = "name"       ;
    public final static String PARAM_SURNAME     = "surname"    ;
    public final static String PARAM_FILEAS      = "fileas"     ;
    public final static String PARAM_ORG         = "org"        ;
    public final static String PARAM_TITLE       = "title"      ;
    public final static String PARAM_GENERALTEL  = "generaltel" ;
    public final static String PARAM_HOMETEL     = "hometel"    ;
    public final static String PARAM_BUSINESSTEL = "businesstel";
    public final static String PARAM_CELL        = "cell"       ;
    public final static String PARAM_FAX         = "fax"        ;
    public final static String PARAM_EMAIL       = "email"      ;
    public final static String PARAM_DISPLAYNAME = "displayname";
    public final static String PARAM_HAS_PHOTO   = "hasphoto"   ;
    public final static String PARAM_PHOTO_TYPE  = "photoType"  ;

    //
    // from-to jsp for calendar (both event and task)
    //
    public final static String PARAM_DESC      = "desc"     ;
    public final static String PARAM_SUMM      = "summary"  ;
    public final static String PARAM_DDSTART   = "ddstart"  ;
    public final static String PARAM_MMSTART   = "mmstart"  ;
    public final static String PARAM_YYYYSTART = "yyyystart";
    public final static String PARAM_HOUSTART  = "houstart" ;
    public final static String PARAM_MINSTART  = "minstart" ;
    public final static String PARAM_DDEND     = "ddend"    ;
    public final static String PARAM_MMEND     = "mmend"    ;
    public final static String PARAM_YYYYEND   = "yyyyend"  ;
    public final static String PARAM_HOUEND    = "houend"   ;
    public final static String PARAM_MINEND    = "minend"   ;
    public final static String PARAM_TYPEEV    = "typeev"   ;
    public final static String PARAM_LOCATION  = "location" ;
    public final static String PARAM_ALLDAY    = "allday"   ;

    //
    // from-to jsp only for task
    //
    public final static String PARAM_PERCENT_COMPLETE = "percentcomplete";
    public final static String PARAM_DDCOMPLETED      = "ddcompleted"    ;
    public final static String PARAM_MMCOMPLETED      = "mmcompleted"    ;
    public final static String PARAM_YYYYCOMPLETED    = "yyyycompleted"  ;

    public final static String OPT_VIEW   = "VIEW"  ;
    public final static String OPT_UPDATE = "UPDATE";
    public final static String OPT_RETURN = "RETURN";
    public final static String OPT_DELETE = "DELETE";
    public final static String OPT_RESET  = "RESET" ;
    public final static String OPT_ADD    = "ADD"   ;
    public final static String OPT_INSERT = "INSERT";

    //
    // Token from contact's file
    //
    public final static String TOKEN_GENERALTEL  = "OtherTelephoneNumber"   ;
    public final static String TOKEN_HOMETEL     = "HomeTelephoneNumber"    ;
    public final static String TOKEN_BUSINESSTEL = "BusinessTelephoneNumber";
    public final static String TOKEN_MOBTEL      = "MobileTelephoneNumber"  ;
    public final static String TOKEN_HOMEFAX     = "HomeFaxNumber"          ;
    public final static String TOKEN_EMAIL1      = "Email1Address"          ;
    public final static String TOKEN_JOBTITLE    = "JobTitle"               ;

    //jsp for contact
    public final static String VIEW_DEFAULT  = "/list.jsp"        ;
    public final static String VIEW_INFOFILE = "/view.jsp"        ;
    public final static String VIEW_ADD      = "/add.jsp"         ;
    public final static String REDIRECT      = "/redirectview.jsp";
    //jsp for calendar event
    public final static String VIEW_DEFCA      = "/listca.jsp"        ;
    public final static String VIEW_INFOFILECA = "/viewca.jsp"        ;
    public final static String VIEW_ADDCA      = "/addca.jsp"         ;
    public final static String REDIRECT_CA     = "/redirectviewca.jsp";
    //jsp for calendar task
    public final static String VIEW_LISTTASK = "/listtask.jsp"    ;
    public final static String VIEW_INFOTASK = "/viewtask.jsp"    ;
    public final static String VIEW_ADDTASK  = "/addtask.jsp"     ;
    public final static String REDIRECT_TASK = "/redirectviewtask.jsp";

    //to jsp
    public final static String ATTR_ID       = "id"      ;
    public final static String ATTR_ITEM     = "item"    ;
    public final static String ATTR_ITEMS    = "items"   ;
    public final static String ATTR_NUMITEMS = "numItems";

    //error message
    public final static String ERR_ITEMNOTFOUND = "error_item";

    public final static String SQL_DELETE_LAST_SYNC_CONTACT =
        "delete from fnbl_last_sync where sync_source='scard' or "      +
        " sync_source='card'"                                           ;
    public final static String SQL_DELETE_CLIENT_MAPPING_CONTACT =
        "delete from fnbl_client_mapping where sync_source='scard' or " +
        " sync_source='card'"                                           ;
    public final static String SQL_DELETE_LAST_SYNC_EVENT =
        "delete from fnbl_last_sync where sync_source='scal' or "       +
        " sync_source='cal' or sync_source='event'"                     ;
    public final static String SQL_DELETE_CLIENT_MAPPING_EVENT =
        "delete from fnbl_client_mapping where sync_source='scal' or "  +
        " sync_source='cal' or sync_source='event' "                    ;
    public final static String SQL_DELETE_LAST_SYNC_TASK =
        "delete from fnbl_last_sync where sync_source='task' or "       +
        " sync_source='stask'"                                          ;
    public final static String SQL_DELETE_CLIENT_MAPPING_TASK =
        "delete from fnbl_client_mapping where sync_source='task' or "  +
        " sync_source='stask'"                                          ;

    // ------------------------------------------------------------ Private data
    private String dataSourceName = null;
    private DataSource dataSource = null;

    /**
     * Establish the type of information to visualize: contact, event or task
     *
     * @param request the HttpServlet Request
     * @return the type of information to visualize
     */
    private String getType(HttpServletRequest request) {
        return (String)request.getParameter(PARAM_TYPE);
    }

    /**
     * Get parameter operation to establish the action to do
     *
     * @param request the HttpServlet Request
     * @return the action to do
     */
    private String getOperation(HttpServletRequest request) {
        return request.getParameter(PARAM_OPT);
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

    // -------------------------------------------------- Servlet Implementation

    /**
     * Initializes the servlet.
     */
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
     * Destroys the servlet.
     */
    public void destroy() {
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException, IOException
     */
    protected void processRequest(HttpServletRequest  request ,
                                  HttpServletResponse response)
    throws ServletException, IOException {

        String next = null;

        try {

            String type      = getType(request);
            String operation = getOperation(request);
            type = (type == null) ? TYPE_CONTACT : type;

            if (operation == null || operation.equalsIgnoreCase(OPT_RETURN)) {
                showAllItems(request, type);

                if (type.equals(TYPE_EVENT)) {
                    next = VIEW_DEFCA;
                } else if (type.equals(TYPE_TASK)) {
                    next = VIEW_LISTTASK;
                } else {
                    next = VIEW_DEFAULT;
                }
            } else if (operation.equalsIgnoreCase(OPT_VIEW)) {
                if (type.equals(TYPE_EVENT)) {
                    viewEvent(request);
                    next = VIEW_INFOFILECA;
                } else if (type.equals(TYPE_TASK)) {
                    viewTask(request);
                    next = VIEW_INFOTASK;
                } else {
                    viewContact(request);
                    next = VIEW_INFOFILE;
                }
            } else if (operation.equalsIgnoreCase(OPT_UPDATE)) {
                if (type.equals(TYPE_EVENT)) {
                    updateEvent(request);
                    String item = request.getParameter(PARAM_ITEM);
                    next = REDIRECT_CA + "?item=" + item;
                } else if (type.equals(TYPE_TASK)) {
                    updateTask(request);
                    String item = request.getParameter(PARAM_ITEM);
                    next = REDIRECT_TASK + "?item=" + item;
                } else {
                    updateContact(request);
                    String item = request.getParameter(PARAM_ITEM);
                    next = REDIRECT + "?item=" + item;
                }
            } else if (operation.equalsIgnoreCase(OPT_DELETE)) {
                if (type.equals(TYPE_EVENT)) {
                    deleteEvent(request);
                    next = VIEW_DEFCA;
                } else if (type.equals(TYPE_TASK)) {
                    deleteTask(request);
                    next = VIEW_LISTTASK;
                } else {
                    deleteContact(request);
                    next = VIEW_DEFAULT;
                }
                showAllItems(request, type);

            } else if (operation.equalsIgnoreCase(OPT_RESET)) {

                if (type.equals(TYPE_EVENT)) {
                    resetEvent(request);
                    next = VIEW_DEFCA;
                } else if (type.equals(TYPE_TASK)) {
                    resetTask(request);
                    next = VIEW_LISTTASK;
                } else {
                    resetContact(request);
                    next = VIEW_DEFAULT;
                }
                showAllItems(request, type);
            } else if (operation.equalsIgnoreCase(OPT_ADD)) {

                if (type.equals(TYPE_EVENT)) {
                    next = VIEW_ADDCA;
                } else if (type.equals(TYPE_TASK)) {
                    next = VIEW_ADDTASK;
                } else {
                    next = VIEW_ADD;
                }
                showAllItems(request, type);
            } else if (operation.equalsIgnoreCase(OPT_INSERT)) {
                if (type.equals(TYPE_EVENT)) {
                    insertEvent(request);
                    next = VIEW_INFOFILECA;
                } else if (type.equals(TYPE_TASK)) {
                    insertTask(request);
                    next = VIEW_INFOTASK;
                } else {
                    insertContact(request);
                    next = VIEW_INFOFILE;
                }
            }
            moveToNextView(request, response, next);
        } catch (Exception e) {
            throw new ServletException("Error in processing the request", e);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException, IOException
     */
    protected void doGet(HttpServletRequest  request ,
                         HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException, IOException
     */
    protected void doPost(HttpServletRequest  request ,
                          HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        processRequest(request, response);
    }

    /**
     * Includes the specified view
     *
     * @param request servlet request
     * @param response servlet response
     * @param nextView next page view
     *
     * @throws ServletException, IOException
     */
    private void moveToNextView(HttpServletRequest  request ,
                                HttpServletResponse response,
                                String              nextView)
    throws ServletException, IOException {
        RequestDispatcher dispatcher =
            getServletContext().getRequestDispatcher(nextView);

        dispatcher.include(request, response);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Views contacts or calendars list
     *
     * @param request servlet request
     * @param type the type of information required (contact or event or task)
     */
    private void showAllItems(HttpServletRequest request, String type)
    throws Exception {

        HashMap[] items = null;

        if (type.equals(TYPE_CONTACT)) {
            PIMContactDAO contactDAO = getContactDAO(request);
            List<String> idContacts = contactDAO.getAllItems();

            items = new HashMap[idContacts.size()];

            for (int i=0; i<idContacts.size(); i++) {
                items[i] = getContact(contactDAO, idContacts.get(i));
            }

        } else if (type.equals(TYPE_EVENT)) {
            PIMCalendarDAO eventDAO = getEventDAO(request);
            List<String> idCalendars = eventDAO.getAllItems();

            items = new HashMap[idCalendars.size()];

            for (int i=0; i<idCalendars.size(); i++) {
                items[i] = getEvent(eventDAO, idCalendars.get(i));
            }
        } else if (type.equals(TYPE_TASK)) {
            PIMCalendarDAO taskDAO = getTaskDAO(request);
            List<String> idCalendars = taskDAO.getAllItems();

            items = new HashMap[idCalendars.size()];

            for (int i=0; i<idCalendars.size(); i++) {
                items[i] = getTask(taskDAO, idCalendars.get(i));
            }
        }

        request.setAttribute(ATTR_ITEMS, items);
        request.setAttribute(ATTR_NUMITEMS, String.valueOf(items.length));
    }

    // -------------------------------------------------------- Contact Handling

    /**
     * Set request attribute with the information about the selected contact.
     * Read the information from the datastore. If the id contact to find is not
     * into datastore, sets into a request an attribute to signalize at the user
     * that the contact was not found because probably it was already deleted.
     *
     * @param request servlet request
     */
    private void viewContact(HttpServletRequest request) throws Exception {
        String idContact = request.getParameter(PARAM_ITEM);

        try {
            request.setAttribute(ATTR_ITEM,
                                 getContact(getContactDAO(request), idContact)
            );
            request.setAttribute(ERR_ITEMNOTFOUND, "");
        } catch(Exception e) {
            request.setAttribute(ERR_ITEMNOTFOUND, idContact);
        }
    }

    /**
     * Gets the Map with the information of the contact to send to jsp.
     *
     * @param contactDAO the PIMContactDAO object
     * @param idContact the identifier of the contact to search into datastore
     *
     * @return contacts the Map with all information of the contact found
     */
    private HashMap getContact(PIMContactDAO contactDAO, String idContact)
    throws ServletException, IOException {
        Contact contact = null;

        HashMap<String,String> contacts = new HashMap<String,String>();
        contacts.put(ATTR_ID, idContact);
        
        try {
            ContactWrapper cw = contactDAO.getItem(idContact);
            if (cw.getStatus() == SyncItemState.DELETED) {
                throw new DAOException("The selected contact does not exist anymore");
            }
            contacts.put(PARAM_HAS_PHOTO, cw.hasPhoto() ? "Y" : "N");
            if (cw.getPhotoType() != null) {
                contacts.put(PARAM_PHOTO_TYPE, String.valueOf(cw.getPhotoType().shortValue()));
            }

            contact = cw.getContact();

        } catch(DAOException e) {
            throw new ServletException(e);
        }
        
        String surname, name = null;
        surname = contact.getName().getLastName().getPropertyValueAsString() ;
        name    = contact.getName().getFirstName().getPropertyValueAsString();
        contacts.put(PARAM_SURNAME, surname);
        contacts.put(PARAM_NAME   , name   );
        contacts.put(PARAM_FILEAS ,
                     contact.getName()
                            .getDisplayName().getPropertyValueAsString()
                    );
        contacts.put(PARAM_ORG    ,
                     contact.getBusinessDetail()
                            .getCompany().getPropertyValueAsString()
                    );

        StringBuffer displayname = new StringBuffer("");
        if (surname != null && !surname.equals("")) {
            displayname.append(surname);
        }
        if (name != null && !name.equals("")) {
            if (displayname.length() != 0) {
                displayname.append(", ");
            }
            displayname.append(name);
        }
        if (displayname.length() == 0) {
            displayname.append("Unknown");
        }
        contacts.put(PARAM_DISPLAYNAME, displayname.toString());

        List<Title> titles = contact.getBusinessDetail().getTitles();
        if (titles != null && !titles.isEmpty()) {
            Title p = titles.get(0);
            contacts.put(PARAM_TITLE, p.getPropertyValueAsString());
        }

        List<Phone> phones = contact.getPersonalDetail().getPhones();
        String value = null;
        if (phones != null) {
            Iterator<Phone> itPhones = phones.iterator();
            while(itPhones.hasNext()) {
                Phone phone = itPhones.next();
                if (phone.getPhoneType() == null) {
                    continue;
                }

                value = phone.getPropertyValueAsString();
                String phoneType = phone.getPhoneType();
                if (phoneType.equalsIgnoreCase(TOKEN_GENERALTEL)) {
                    contacts.put(PARAM_GENERALTEL, value);
                } else if (phoneType.equalsIgnoreCase(TOKEN_HOMETEL)) {
                    contacts.put(PARAM_HOMETEL, value);
                } else if (phoneType.equalsIgnoreCase(TOKEN_MOBTEL)) {
                    contacts.put(PARAM_CELL, value);
                } else if (phoneType.equalsIgnoreCase(TOKEN_HOMEFAX)) {
                    contacts.put(PARAM_FAX , value);
                }
            }
        }

        List<Phone> businessphones = contact.getBusinessDetail().getPhones();
        if (businessphones != null) {
            Iterator<Phone> itBusinessPhones = businessphones.iterator();
            while(itBusinessPhones.hasNext()) {
                Phone phone = itBusinessPhones.next();
                if (phone.getPhoneType() == null) {
                    continue;
                }
                if (phone.getPhoneType().equalsIgnoreCase(TOKEN_BUSINESSTEL)) {
                    contacts.put(PARAM_BUSINESSTEL, phone.getPropertyValueAsString());
                }
            }
        }

        List<Email> emails = contact.getPersonalDetail().getEmails();
        if (emails != null && !emails.isEmpty()) {
            Email email = new Email();
            int size = emails.size();
            for (int i=0; i<size; i++) {
                email = emails.get(i);
                if (email.getEmailType().equals(TOKEN_EMAIL1)) {
                    contacts.put(PARAM_EMAIL, email.getPropertyValueAsString());
                    break;
                }
            }
        }

        return contacts;
    }

    /**
     * Checks if the contact DAO is null: if it is null, then it will be created
     * and will be saved in the session.
     *
     * @param request the HttpServlet Request
     * @return contactDAO an instace on PIMContactDAO
     */
    private PIMContactDAO getContactDAO(HttpServletRequest request) {
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
     * Inserts contact into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void insertContact(HttpServletRequest request) throws Exception {
        Contact contact = new Contact();
        populateContactFromRequest(request, contact);

        ContactWrapper cw =
            new ContactWrapper(null, getUser(request).getUsername(), contact);

        getContactDAO(request).addItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getContact(getContactDAO(request), cw.getId()));
    }

    /**
     * Updates contact into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void updateContact(HttpServletRequest request) throws Exception {

        String idContact = request.getParameter(PARAM_ITEM);

        Contact contact = null;
        try {

            ContactWrapper cw = getContactDAO(request).getItem(idContact);
            contact = cw.getContact();

        } catch(DAOException e) {
            throw new ServletException(e);
        }

        populateContactFromRequest(request, contact);

        ContactWrapper cw =
            new ContactWrapper(idContact                     ,
                               getUser(request).getUsername(),
                               contact                       );

        getContactDAO(request).updateItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getContact(getContactDAO(request), cw.getId()));
    }

    /**
     * Populates contact object with the informations get from the request.
     *
     * @param request the HttpServlet Request
     * @param contact the Contact object to populate with the value's parameters
     *                of the request.
     */
    private void populateContactFromRequest(HttpServletRequest request,
                                            Contact            contact)
    throws Exception {

        String operation = getOperation(request);

        String name        = (String)request.getParameter(PARAM_NAME);
        String surname     = (String)request.getParameter(PARAM_SURNAME);
        String fileas      = (String)request.getParameter(PARAM_FILEAS);
        String org         = (String)request.getParameter(PARAM_ORG);
        String title       = (String)request.getParameter(PARAM_TITLE);
        String generaltel  = (String)request.getParameter(PARAM_GENERALTEL);
        String hometel     = (String)request.getParameter(PARAM_HOMETEL);
        String businesstel = (String)request.getParameter(PARAM_BUSINESSTEL);
        String cell        = (String)request.getParameter(PARAM_CELL);
        String fax         = (String)request.getParameter(PARAM_FAX);
        String email       = (String)request.getParameter(PARAM_EMAIL);

        contact.getName().getFirstName().setPropertyValue(name);
        contact.getName().getLastName().setPropertyValue(surname);
        contact.getBusinessDetail().getCompany().setPropertyValue(org);

        if (operation.equalsIgnoreCase(OPT_INSERT)) {
            if (fileas == null || fileas.equals("")) {
                fileas = "";
                if (surname != null && !surname.equals("")) {
                    fileas = surname;
                }
                if (name != null && !name.equals("")) {
                    if (!"".equals(fileas)) {
                        fileas += ", ";
                    }
                    fileas += name;
                }
            }
        }
        contact.getName().getDisplayName().setPropertyValue(fileas);

        Title t = new Title();
        t.setTitleType(TOKEN_JOBTITLE);
        t.setPropertyValue(title);
        contact.getBusinessDetail().addTitle(t);

        Phone p = null;

        p = new Phone();
        p.setPhoneType(TOKEN_GENERALTEL);
        p.setPropertyValue(generaltel);
        contact.getPersonalDetail().addPhone(p);

        p = new Phone();
        p.setPhoneType(TOKEN_HOMETEL);
        p.setPropertyValue(hometel);
        contact.getPersonalDetail().addPhone(p);

        p = new Phone();
        p.setPhoneType(TOKEN_MOBTEL);
        p.setPropertyValue(cell);
        contact.getPersonalDetail().addPhone(p);

        p = new Phone();
        p.setPhoneType(TOKEN_HOMEFAX);
        p.setPropertyValue(fax);
        contact.getPersonalDetail().addPhone(p);

        p = new Phone();
        p.setPhoneType(TOKEN_BUSINESSTEL);
        p.setPropertyValue(businesstel);
        contact.getBusinessDetail().addPhone(p);

        Email email1 = new Email();
        email1.setEmailType(TOKEN_EMAIL1);
        email1.setPropertyValue(email);
        contact.getPersonalDetail().addEmail(email1);
    }

    /**
     * Deletes the contact with the id specified by the request parameter
     * PARAM_ITEM
     *
     * @param request servlet request
     */
    private void deleteContact(HttpServletRequest request) throws Exception {
        String idContact = request.getParameter(PARAM_ITEM);
        deleteContact(request, idContact);
    }

    /**
     * Deletes the contact with the identifier pass as input param
     *
     * @param request   the HttpServlet Request
     * @param idContact the contact's identifier
     */
    private void deleteContact(HttpServletRequest request, String idContact)
    throws Exception {
        getContactDAO(request).
            removeItem(idContact, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Delete all contacts and remove the last sync and mappings.
     *
     * @param request servlet request
     *
     * @throws Exception in case of any error
     */
    private void resetContact(HttpServletRequest request) throws Exception {

        PIMContactDAO contactDAO = getContactDAO(request);

        //
        // Delete all contacts
        //
        List<String> idContacts = contactDAO.getAllItems();
        for (int i=0; i< idContacts.size(); i++) {
            deleteContact(request, idContacts.get(i));
        }

        //
        // Reset last sync and mappings
        // This is particularly ugly, since it accesses directly the database,
        // but for now we have no other choice.
        //
        //
        Connection c = dataSource.getConnection();

        Statement s = c.createStatement();
        s.executeUpdate(SQL_DELETE_LAST_SYNC_CONTACT);
        s.close(); s = null;

        s = c.createStatement();
        s.executeUpdate(SQL_DELETE_CLIENT_MAPPING_CONTACT);
        s.close(); s = null;

        c.close();
    }

    // ------------------------------------------------------- Calendar Handling

    //
    // Calendar Event
    //

    /**
     * Check if the event DAO is null: if it is null, then it will be created
     * and will be saved in the session.
     *
     * @param request the HttpServlet Request
     * @return eventDAO an instace on PIMCalendarDAO
     */
    private PIMCalendarDAO getEventDAO(HttpServletRequest request) {
        Sync4jUser user = getUser(request);
        PIMCalendarDAO eventDAO =
            (PIMCalendarDAO)request.getSession().getAttribute("eventDAO");

        if (eventDAO == null) {
            eventDAO = new PIMCalendarDAO(user.getUsername(),
                                          Event.class       );
            request.getSession().setAttribute("eventDAO", eventDAO);
        }
        return eventDAO;
    }

    /**
     * Set request attribute with the information about the selected event.
     * Read the information from the datastore. If the id event to find is
     * not into datastore, sets into a request an attribute to signalize at the
     * user that the event was not found because probably it was already
     * deleted.
     *
     * @param request servlet request
     */
    private void viewEvent(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);

        try {
            request.setAttribute(ATTR_ITEM,
                                 getEvent(getEventDAO(request), idItem)
            );
            request.setAttribute(ERR_ITEMNOTFOUND, "");
        } catch(Exception e) {
            request.setAttribute(ERR_ITEMNOTFOUND, idItem);
        }
    }

    /**
     * Inserts event into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void insertEvent(HttpServletRequest request) throws Exception {
        Calendar calendar = new Calendar(new Event());

        populateCalendarEventFromRequest(request, calendar);
        CalendarWrapper cw =
            new CalendarWrapper(null, getUser(request).getUsername(), calendar);

        getEventDAO(request).addItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getEvent(getEventDAO(request), cw.getId()));
    }

    /**
     * Updates event into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void updateEvent(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);

        Calendar calendar = null;
        try {

            CalendarWrapper cw = getEventDAO(request).getItem(idItem);
            calendar = cw.getCalendar();

        } catch(DAOException e) {
            throw new ServletException(e);
        }

        populateCalendarEventFromRequest(request, calendar);

        CalendarWrapper cw =
            new CalendarWrapper(idItem                        ,
                                getUser(request).getUsername(),
                                calendar                      );

        getEventDAO(request).updateItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getEvent(getEventDAO(request), cw.getId()));
    }

    /**
     * Deletes the event with the id specified by the request
     * parameter PARAM_ITEM
     *
     * @param request servlet request
     */
    private void deleteEvent(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);
        deleteEvent(request, idItem);
    }

    /**
     * Deletes the event with the identifier pass as input param
     *
     * @param request the HttpServlet Request
     * @param idItem  the event's identifier
     */
    private void deleteEvent(HttpServletRequest request, String idItem)
    throws Exception {
        getEventDAO(request).
            removeItem(idItem, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Delete all events and remove the last sync and mappings.
     *
     * @param request servlet request
     *
     * @throws Exception in case of any error
     */
    private void resetEvent(HttpServletRequest request) throws Exception {

        PIMCalendarDAO eventDAO = getEventDAO(request);

        //
        // Delete all events
        //
        List<String> idItems = eventDAO.getAllItems();
        for (int i=0; i< idItems.size(); i++) {
            deleteEvent(request, idItems.get(i));
        }

        //
        // Reset last sync and mappings
        // This is particularly ugly, since it accesses directly the database,
        // but for now we have no other choice.
        //
        Connection c = dataSource.getConnection();

        Statement s = c.createStatement();

        s.executeUpdate(SQL_DELETE_LAST_SYNC_EVENT);
        s.close(); s = null;

        s = c.createStatement();
        s.executeUpdate(SQL_DELETE_CLIENT_MAPPING_EVENT);
        s.close(); s = null;

        c.close();
    }

    /**
     * Gets the Map with the information of the event to send to jsp.
     *
     * @param eventDAO the PIMCalendarDAO object
     * @param idItem the identifier of the event to search into datastore
     *
     * @return the Map with all information of the event found
     */
    private HashMap getEvent(PIMCalendarDAO eventDAO, String idItem)
    throws ServletException, IOException {
        CalendarContent calendar = null;

        try {
            CalendarWrapper cw = eventDAO.getItem(idItem);
            if (cw.getStatus() == SyncItemState.DELETED) {
                throw new DAOException("The selected event does not exist anymore");
            }
            calendar = cw.getCalendar().getCalendarContent();

        } catch(DAOException e) {
            throw new ServletException(e);
        }

        return getBaseInfo(calendar, idItem);
    }

    //
    // Calendar Task
    //

    /**
     * Check if the task DAO is null: if it is null, then it will be created
     * and will be saved in the session.
     *
     * @param request the HttpServlet Request
     * @return taskDAO an instace on PIMCalendarDAO
     */
    private PIMCalendarDAO getTaskDAO(HttpServletRequest request) {
        Sync4jUser user = getUser(request);
        PIMCalendarDAO taskDAO =
            (PIMCalendarDAO)request.getSession().getAttribute("taskDAO");

        if (taskDAO == null) {
            taskDAO = new PIMCalendarDAO(user.getUsername(),
                                         Task.class        );
            request.getSession().setAttribute("taskDAO", taskDAO);
        }
        return taskDAO;
    }

    /**
     * Set request attribute with the information about the selected task.
     * Read the information from the datastore. If the id task to find is
     * not into datastore, sets into a request an attribute to signalize at the
     * user that the task was not found because probably it was already
     * deleted.
     *
     * @param request servlet request
     */
    private void viewTask(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);

        try {
            request.setAttribute(ATTR_ITEM,
                                 getTask(getTaskDAO(request), idItem)
            );
            request.setAttribute(ERR_ITEMNOTFOUND, "");
        } catch(Exception e) {
            request.setAttribute(ERR_ITEMNOTFOUND, idItem);
        }
    }

    /**
     * Inserts task into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void insertTask(HttpServletRequest request) throws Exception {
        Calendar calendar = new Calendar(new Task());

        populateCalendarTaskFromRequest(request, calendar);
        CalendarWrapper cw =
            new CalendarWrapper(null, getUser(request).getUsername(), calendar);

        getTaskDAO(request).addItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getTask(getTaskDAO(request), cw.getId()));
    }

    /**
     * Updates task into datastore.
     *
     * @param request the HttpServlet Request
     */
    private void updateTask(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);

        Calendar calendar = null;
        try {

            CalendarWrapper cw = getTaskDAO(request).getItem(idItem);
            calendar = cw.getCalendar();

        } catch(DAOException e) {
            throw new ServletException(e);
        }

        populateCalendarTaskFromRequest(request, calendar);

        CalendarWrapper cw =
            new CalendarWrapper(idItem                        ,
                                getUser(request).getUsername(),
                                calendar);

        getTaskDAO(request).updateItem(cw);

        request.setAttribute(ATTR_ITEM,
                             getTask(getTaskDAO(request), cw.getId()));
    }

    /**
     * Deletes the task with the id specified by the request
     * parameter PARAM_ITEM
     *
     * @param request servlet request
     */
    private void deleteTask(HttpServletRequest request) throws Exception {
        String idItem = request.getParameter(PARAM_ITEM);
        deleteTask(request, idItem);
    }

    /**
     * Deletes the task with the identifier pass as input param
     *
     * @param request the HttpServlet Request
     * @param idItem  the event's identifier
     */
    private void deleteTask(HttpServletRequest request, String idItem)
    throws Exception {
        getTaskDAO(request).
            removeItem(idItem, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Delete all tasks and remove the last sync and mappings.
     *
     * @param request servlet request
     *
     * @throws Exception in case of any error
     */
    private void resetTask(HttpServletRequest request) throws Exception {

        PIMCalendarDAO taskDAO = getTaskDAO(request);

        //
        // Delete all tasks
        //
        List<String> idItems = taskDAO.getAllItems();
        for (int i=0; i< idItems.size(); i++) {
            deleteTask(request, idItems.get(i));
        }

        //
        // Reset last sync and mappings
        // This is particularly ugly, since it accesses directly the database,
        // but for now we have no other choice.
        //
        Connection c = dataSource.getConnection();

        Statement s = c.createStatement();

        s.executeUpdate(SQL_DELETE_LAST_SYNC_TASK);
        s.close(); s = null;

        s = c.createStatement();
        s.executeUpdate(SQL_DELETE_CLIENT_MAPPING_TASK);
        s.close(); s = null;

        c.close();
    }

    /**
     * Gets the Map with the information of the task to send to jsp.
     *
     * @param taskDAO the PIMCalendarDAO object
     * @param idItem the identifier of the task to search into datastore
     *
     * @return the Map with all information of the task found
     */
    private HashMap getTask(PIMCalendarDAO taskDAO, String idItem)
    throws ServletException, IOException {
        CalendarContent calendar = null;

        try {
            CalendarWrapper cw = taskDAO.getItem(idItem);
            if (cw.getStatus() == SyncItemState.DELETED) {
                throw new DAOException("The selected event does not exist anymore");
            }
            calendar = cw.getCalendar().getCalendarContent();

        } catch(DAOException e) {
            throw new ServletException(e);
        }

        return getTaskInfo(calendar, idItem);
    }

    /**
     * Gets the Map with the information of the task to send to jsp.
     *
     * @param calendarDAO the PIMCalendarDAO object
     * @param idItem the identifier of the calendar to search into datastore
     *
     * @return calendars the Map with all information of the calendar found
     */
    private HashMap getTaskInfo(CalendarContent calendar, String idItem)
    throws ServletException, IOException {

        HashMap<String,String> taskInfo = getBaseInfo(calendar, idItem);

        Task t = (Task)calendar;
        taskInfo.put(PARAM_PERCENT_COMPLETE,
                     t.getPercentComplete().getPropertyValueAsString());

        String dtcompleted = t.getDateCompleted().getPropertyValueAsString();
        if (dtcompleted != null && !dtcompleted.equals("")) {
            //
            // Check if the calendar is an allday task or not
            //
            java.util.Calendar cal = null;
            if (calendar.isAllDay()) {
                //
                // The accepted format for all day event is YYYYMMDD'T'HHmmss
                //
                cal = getCalendarFromAllDay(dtcompleted);
            } else {
                cal = getCalendarFromUTC(dtcompleted);
            }

            int year  = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            int day   = cal.get(java.util.Calendar.DAY_OF_MONTH);

            String dayCompleted = null;
            if (day < 10) {
                dayCompleted = "0" + day;
            } else {
                dayCompleted = "" + day;
            }

            String monthCompleted = null;
            if (month < 10) {
                monthCompleted = "0" + month;
            } else {
                monthCompleted = "" + month;
            }

            String yearCompleted = "" + year;

            taskInfo.put(PARAM_DDCOMPLETED  , dayCompleted  );
            taskInfo.put(PARAM_MMCOMPLETED  , monthCompleted);
            taskInfo.put(PARAM_YYYYCOMPLETED, yearCompleted );
        }

        return taskInfo;
    }

    /**
     * Populates calendar object with the informations get from the request.
     *
     * @param request the HttpServlet Request
     * @param calendar the Calendar object to populate with the value's
     *                 parameters of the request.
     */
    private void populateCalendarTaskFromRequest(HttpServletRequest request ,
                                                 Calendar           calendar)
    throws Exception {

        String desc     = (String)request.getParameter(PARAM_DESC     );
        String summary  = (String)request.getParameter(PARAM_SUMM     );
        String ddstart  = (String)request.getParameter(PARAM_DDSTART  );
        String mmstart  = (String)request.getParameter(PARAM_MMSTART  );
        String yyyystart= (String)request.getParameter(PARAM_YYYYSTART);
        String ddend    = (String)request.getParameter(PARAM_DDEND    );
        String mmend    = (String)request.getParameter(PARAM_MMEND    );
        String yyyyend  = (String)request.getParameter(PARAM_YYYYEND  );

        //
        // The tasks are always all day because into jsp is not possible to set
        // the date time
        //

        //
        //Start Date
        //
        String dtStart =
            composeDateTime(yyyystart, mmstart, ddstart, "00", "00");
        String dtEnd = composeDateTime(yyyyend, mmend, ddend, "23", "59");

        calendar.getCalendarContent().setAllDay(Boolean.TRUE);

        calendar.getCalendarContent().getDescription().setPropertyValue(desc);
        calendar.getCalendarContent().getSummary().setPropertyValue(summary);
        calendar.getCalendarContent().getDtStart().setPropertyValue(dtStart);
        calendar.getCalendarContent().getDtEnd().setPropertyValue(dtEnd);

        String percentcomplete =
            (String)request.getParameter(PARAM_PERCENT_COMPLETE);
        String ddcompleted   = (String)request.getParameter(PARAM_DDCOMPLETED );
        String mmcompleted   = (String)request.getParameter(PARAM_MMCOMPLETED );
        String yyyycompleted = (String)request.getParameter(PARAM_YYYYCOMPLETED);

        ((Task)calendar.getCalendarContent()).getPercentComplete()
                                             .setPropertyValue(percentcomplete);

        //
        // Date Completed: the time is always 00:00 because is not possible to
        // set it from jsp
        //
        String dtCompleted =
            composeDateTime(yyyycompleted, mmcompleted, ddcompleted, "00", "00");

        ((Task)calendar.getCalendarContent()).getDateCompleted()
                                             .setPropertyValue(dtCompleted);
    }

    //
    // Common
    //

    /**
     * Gets the Map with the information of the calendar to send to jsp.
     *
     * @param calendarDAO the PIMCalendarDAO object
     * @param idCalendar the identifier of the calendar to search into datastore
     *
     * @return calendars the Map with all information of the calendar found
     */
    private HashMap getBaseInfo(CalendarContent calendar, String idItem)
    throws ServletException, IOException {

        HashMap<String,String> calendars = new HashMap<String,String>();
        calendars.put(ATTR_ID, idItem);

        String desc = calendar.getDescription().getPropertyValueAsString();
        calendars.put(PARAM_DESC, desc);

        String summary = calendar.getSummary().getPropertyValueAsString();
        calendars.put(PARAM_SUMM, summary);

        String dtstart = (String)calendar.getDtStart().getPropertyValue();
        if (dtstart != null && !dtstart.equals("")) {
            //
            // Check if the calendar is an allday event or not
            //
            java.util.Calendar cal = null;
            if (calendar.isAllDay()) {
                //
                // The accepted format for all day event is YYYYMMDD'T'HHmmss
                //
                cal = getCalendarFromAllDay(dtstart);
            } else {
                cal = getCalendarFromUTC(dtstart);
            }

            int year  = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            int day   = cal.get(java.util.Calendar.DAY_OF_MONTH);

            String dayStart = null;
            if (day < 10) {
                dayStart = "0" + day;
            } else {
                dayStart = "" + day;
            }

            String monthStart = null;
            if (month < 10) {
                monthStart = "0" + month;
            } else {
                monthStart = "" + month;
            }

            String yearStart = "" + year;

            String hour = null, min = null;
            int hours = cal.get(java.util.Calendar.HOUR_OF_DAY);

            if (hours < 10) {
                hour = "0" + hours;
            } else {
                hour = "" + hours;
            }

            int mins = cal.get(java.util.Calendar.MINUTE);
            if (mins < 10) {
                min = "0" + mins;
            } else {
                min = "" + mins;
            }

            calendars.put(PARAM_DDSTART,dayStart);
            calendars.put(PARAM_MMSTART,monthStart);
            calendars.put(PARAM_YYYYSTART,yearStart);

            calendars.put(PARAM_HOUSTART,hour);
            calendars.put(PARAM_MINSTART,min);
        }

        String dtend = calendar.getDtEnd().getPropertyValueAsString();
        if (dtend != null && !dtend.equals("")) {
            //
            // Check if the calendar is an allday event or not
            //
            java.util.Calendar cal = null;
            if (calendar.isAllDay()) {
                //
                // The accepted format for all day event is YYYYMMDD'T'HHmmss
                //
                cal = getCalendarFromAllDay(dtend);
            } else {
                cal = getCalendarFromUTC(dtend);
            }

            int year  = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            int day   = cal.get(java.util.Calendar.DAY_OF_MONTH);

            String dayEnd = null;
            if (day < 10) {
                dayEnd = "0" + day;
            } else {
                dayEnd = "" + day;
            }

            String monthEnd = null;
            if (month < 10) {
                monthEnd = "0" + month;
            } else {
                monthEnd = "" + month;
            }

            String yearEnd = "" + year;

            String hour = null, min = null;
            int hours = cal.get(java.util.Calendar.HOUR_OF_DAY);
            if (hours < 10) {
                hour = "0" + hours;
            } else {
                hour = "" + hours;
            }

            int mins = cal.get(java.util.Calendar.MINUTE);
            if (mins < 10) {
                min = "0" + mins;
            } else {
                min = "" + mins;
            }

            calendars.put(PARAM_DDEND,dayEnd);
            calendars.put(PARAM_MMEND,monthEnd);
            calendars.put(PARAM_YYYYEND,yearEnd);

            calendars.put(PARAM_HOUEND,hour);
            calendars.put(PARAM_MINEND,min);
        }

        String typeev = calendar.getCategories().getPropertyValueAsString();
        calendars.put(PARAM_TYPEEV,typeev);

        String location = calendar.getLocation().getPropertyValueAsString();
        calendars.put(PARAM_LOCATION,location);

        if (calendar.isAllDay()) {
            calendars.put(PARAM_ALLDAY, "true");
        } else {
            calendars.put(PARAM_ALLDAY, "false");
        }

        return calendars;
    }

    /**
     * Populates calendar object with the informations get from the request.
     *
     * @param request the HttpServlet Request
     * @param calendar the Calendar object to populate with the value's
     *                 parameters of the request.
     */
    private void populateCalendarEventFromRequest(HttpServletRequest request ,
                                                  Calendar           calendar)
    throws Exception {

        String desc     = (String)request.getParameter(PARAM_DESC);
        String summary  = (String)request.getParameter(PARAM_SUMM);
        String ddstart  = (String)request.getParameter(PARAM_DDSTART);
        String mmstart  = (String)request.getParameter(PARAM_MMSTART);
        String yyyystart= (String)request.getParameter(PARAM_YYYYSTART);
        String houstart = (String)request.getParameter(PARAM_HOUSTART);
        String minstart = (String)request.getParameter(PARAM_MINSTART);
        String ddend    = (String)request.getParameter(PARAM_DDEND);
        String mmend    = (String)request.getParameter(PARAM_MMEND);
        String yyyyend  = (String)request.getParameter(PARAM_YYYYEND);
        String houend   = (String)request.getParameter(PARAM_HOUEND);
        String minend   = (String)request.getParameter(PARAM_MINEND);
        String type     = (String)request.getParameter(PARAM_TYPEEV);
        String location = (String)request.getParameter(PARAM_LOCATION);
        String allday   = (String)request.getParameter(PARAM_ALLDAY);

        String start = null, end = null;

        //
        //Start Date
        //
        String dateStart =
            composeDateTime(yyyystart, mmstart, ddstart, houstart, minstart);
        String dateEnd = composeDateTime(yyyyend, mmend, ddend, houend, minend);

        if (allday != null && allday.equals("on")) {
            calendar.getCalendarContent().setAllDay(Boolean.TRUE);

            start = dateStart;
            if (dateEnd != null) {
                end = dateEnd.replaceAll("T0000", "T2359");
            }

        } else {
            calendar.getCalendarContent().setAllDay(Boolean.FALSE);
            //
            // Create the UTC string
            //
            start = getStringDateInUTC(dateStart);
            end   = getStringDateInUTC(dateEnd  );
        }

        calendar.getCalendarContent().getDescription().setPropertyValue(desc);
        calendar.getCalendarContent().getSummary().setPropertyValue(summary);
        calendar.getCalendarContent().getDtStart().setPropertyValue(start);
        calendar.getCalendarContent().getDtEnd().setPropertyValue(end);
        calendar.getCalendarContent().getCategories().setPropertyValue(type);
        calendar.getCalendarContent().getLocation().setPropertyValue(location);

    }

    /**
     * Return a Calendar into local time format from UTC format.
     *
     * @param dateInput the date into UTC format.
     *
     * @retun cal an object java.util.Calendar into local timezone
     */
    private static java.util.Calendar getCalendarFromUTC(String dateInput) {
        java.util.Calendar cal = null;
        try {
            SimpleDateFormat f = new SimpleDateFormat(TimeUtils.PATTERN_UTC);
            f.setLenient(false);
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date date = f.parse(dateInput);
            cal = java.util.Calendar.getInstance();
            cal.setTime(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    /**
     * Return a Calendar in local time format from date into all day format.
     *
     * @param dateInput the input date not in UTC format
     *
     * @return cal an object java.util.Calendar into local timezone.
     */
    private static java.util.Calendar getCalendarFromAllDay(String dateInput) {
        java.util.Calendar cal = null;
        try {

            SimpleDateFormat f = new SimpleDateFormat(TimeUtils.PATTERN_UTC_WOZ);
            f.setLenient(false);
            java.util.Date date = f.parse(dateInput);
            cal = java.util.Calendar.getInstance();
            cal.setTime(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    /**
     * Returns a string composed by date and time (plus 00 for seconds).
     *
     * @param yyyy the year
     * @param mm   the month
     * @param dd   the day
     * @param hour the hour
     * @param min  the minute
     *
     * @return the string date into format yyyyMMdd'T'HHmmss
     */
    private String composeDateTime(String yyyy,
                                   String mm  ,
                                   String dd  ,
                                   String hour,
                                   String min ) {

        StringBuffer date = new StringBuffer();

        if ("".equals(yyyy) && "".equals(mm) && "".equals(dd)) {
            return "";
        } else {
            date.append(yyyy);

            if (Integer.parseInt(mm) < 10) {
                if (mm.length() == 1) {
                    date.append('0');
                }
            }
            date.append(mm);

            if (Integer.parseInt(dd) < 10) {
                if (dd.length() == 1) {
                    date.append('0');
                }
            }
            date.append(dd )
                .append('T');

            if (hour == null || hour.equals("")) {
                hour = "00";
            } else if (Integer.parseInt(hour) < 10) {
                if (hour.length() == 1) {
                    date.append('0');
                }
            }
            date.append(hour);

            if (min == null || min.equals("")) {
                min = "00";
            } else if (Integer.parseInt(min) < 10) {
                if (min.length() == 1) {
                    date.append('0');
                }
            }
            date.append(min);
            //
            //Added 00 for seconds
            //
            date.append("00");
        }
        return date.toString();
    }

    /**
     * Returns a string to represent a date into UTC format
     * (yyyyMMdd'T'HHmmss'Z')
     *
     * @param date the string date in local time
     * @return the date into UTC format
     */
    private String getStringDateInUTC(String date) throws Exception {
        SimpleDateFormat f = new SimpleDateFormat(TimeUtils.PATTERN_UTC);
        if (date != null && !"".equals(date)) {
            java.util.Date d = f.parse(date + 'Z');
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            return f.format(d);
        }
        return "";
    }
}
