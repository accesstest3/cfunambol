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
package com.funambol.foundation.items.dao;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.TypifiedProperty;
import com.funambol.common.pim.contact.*;

import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DBTools;

import com.funambol.server.config.Configuration;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.ContactWrapper;
import com.funambol.foundation.util.Def;

/**
 * This class implements methods to access PIM contact data in a data store.
 *
 * @version $Id$
 */
public class PIMContactDAO extends EntityDAO {

    // --------------------------------------------------------------- Constants

    private static final String SQL_ORDER_BY_ID = "ORDER BY id";
    private static final String SQL_FILTER_BY_CONTACT_TYPE =
            "WHERE contact = ? AND type = ? ";
    private static final String SQL_GET_FNBL_PIM_CONTACT =
            "SELECT id, userid, last_update, status, photo_type, importance, sensitivity, "
            + "subject, folder, anniversary, first_name, middle_name, "
            + "last_name, display_name, birthday, body, categories, children, "
            + "hobbies, initials, languages, nickname, spouse, suffix, title, "
            + "assistant, company, department, job_title, manager, mileage, "
            + "office_location, profession, companies, gender "
            + "FROM fnbl_pim_contact ";
    private static final String SQL_GET_FNBL_PIM_CONTACT_ID_LIST =
            "SELECT id FROM fnbl_pim_contact ";
    private static final String SQL_GET_FNBL_PIM_CONTACT_ID_LIST_BY_USER =
            SQL_GET_FNBL_PIM_CONTACT_ID_LIST
            + "WHERE userid = ? "
            + "AND status <> 'D' ";
    private static final String SQL_GET_FNBL_PIM_CONTACT_BY_ID_USER =
            SQL_GET_FNBL_PIM_CONTACT
            + "WHERE id = ? AND userid = ? ";
    private static final String SQL_GET_CONTACT_ID_BY_ID_AND_USER_ID =
            "select id from fnbl_pim_contact where id=? and userid=?";
    private static final String SQL_GET_STATUS_BY_ID_USER_TIME =
            "SELECT status FROM fnbl_pim_contact "
            + "WHERE id = ? AND userid = ? AND last_update > ? ";
    private static final String SQL_GET_FNBL_PIM_CONTACT_ITEM_BY_ID =
            "SELECT type, value FROM fnbl_pim_contact_item WHERE contact = ? "
            + "ORDER BY type";
    private static final String SQL_GET_FNBL_PIM_ADDRESS_BY_ID =
            "SELECT type, street, city, state, postal_code, country, po_box, " +
            "extended_address FROM fnbl_pim_address WHERE contact = ? ";
    private static final String SQL_CHECK_IF_IN_FNBL_PIM_CONTACT_ITEM =
            "SELECT contact FROM fnbl_pim_contact_item "
            + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_SELECT_FROM_FNBL_PIM_ADDRESS =
            "SELECT contact FROM fnbl_pim_address "
            + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_SELECT_FROM_FNBL_PIM_CONTACT_PHOTO =
            "SELECT p.contact, p.type, p.photo, p.url FROM fnbl_pim_contact c, " +
            "fnbl_pim_contact_photo p where c.id = ?  and c.userid = ? and " +
            "c.status != 'D' and c.id = p.contact";
    private static final String SQL_GET_CHANGED_CONTACTS_BY_USER_AND_LAST_UPDATE =
            "select id,status from fnbl_pim_contact where userid=? and " +
            "last_update>? and last_update<? order by id";
    private static final String SQL_INSERT_INTO_FNBL_PIM_CONTACT =
            "INSERT INTO fnbl_pim_contact "
            + "(id, userid, last_update, status, photo_type, importance, sensitivity, "
            + "subject, folder, anniversary, first_name, middle_name, "
            + "last_name, display_name, birthday, body, categories, children, "
            + "hobbies, initials, languages, nickname, spouse, suffix, title, "
            + "assistant, company, department, job_title, manager, mileage, "
            + "office_location, profession, companies, gender ) "
            + "VALUES "
            + "(?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    private static final String SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM =
            "INSERT INTO fnbl_pim_contact_item "
            + "(contact, type, value) "
            + "VALUES (?, ?, ?) ";
    private static final String SQL_INSERT_INTO_FNBL_PIM_ADDRESS =
            "INSERT INTO fnbl_pim_address "
            + "(contact, type, street, city, state, postal_code, country, "
            + "po_box, extended_address) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    private static final String SQL_INSERT_INTO_FNBL_PIM_CONTACT_PHOTO =
            "INSERT INTO fnbl_pim_contact_photo (contact, type, photo, url) VALUES (?,?,?,?)";
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_BEGIN =
            "UPDATE fnbl_pim_contact SET ";
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_END =
            " WHERE id = ? AND userid = ? ";
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_STATUS =
            SQL_UPDATE_FNBL_PIM_CONTACT_BEGIN + "status = ?, last_update = ? " +
            SQL_UPDATE_FNBL_PIM_CONTACT_END;
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_PHOTO_TYPE =
            SQL_UPDATE_FNBL_PIM_CONTACT_BEGIN +
            "status = ?, last_update = ?, photo_type = ? " +
            SQL_UPDATE_FNBL_PIM_CONTACT_END;
    private static final String SQL_UPDATE_FNBL_PIM_ADDRESS_BEGIN =
            "UPDATE fnbl_pim_address SET ";
    private static final String SQL_UPDATE_FNBL_PIM_ADDRESS_END =
            " " + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_ITEM =
            "UPDATE fnbl_pim_contact_item SET value = ? "
            + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_UPDATE_FNBL_PIM_CONTACT_PHOTO =
            "UPDATE fnbl_pim_contact_photo SET type = ?, url = ?, photo = ? where contact = ?";
    private static final String SQL_DELETE_FNBL_PIM_CONTACT_ITEM =
            "DELETE FROM fnbl_pim_contact_item "
            + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_DELETE_CONTACT_BY_ID_USERID =
            "UPDATE fnbl_pim_contact SET status = 'D', last_update = ? "
            + "WHERE id = ? AND userid = ? ";
    private static final String SQL_DELETE_CONTACTS_BY_USERID =
            "UPDATE fnbl_pim_contact SET status = 'D', last_update = ? "
            + "WHERE status <> 'D' AND userid = ?";
    private static final String SQL_DELETE_FNBL_PIM_CONTACT_PHOTO =
            "DELETE FROM fnbl_pim_contact_photo WHERE contact = ?";

    private static final String SQL_EQUALS_QUESTIONMARK = " = ?";
    private static final String SQL_EQUALS_QUESTIONMARK_COMMA = " = ?, ";

    protected static final String SQL_FIELD_ID = "id";
    protected static final String SQL_FIELD_USERID = "userid";
    protected static final String SQL_FIELD_LAST_UPDATE = "last_update";
    protected static final String SQL_FIELD_STATUS = "status";
    protected static final String SQL_FIELD_PHOTO_TYPE = "photo_type";
    protected static final String SQL_FIELD_IMPORTANCE = "importance";
    protected static final String SQL_FIELD_SENSITIVITY = "sensitivity";
    protected static final String SQL_FIELD_SUBJECT = "subject";
    protected static final String SQL_FIELD_FOLDER = "folder";
    protected static final String SQL_FIELD_ANNIVERSARY = "anniversary";
    protected static final String SQL_FIELD_FIRST_NAME = "first_name";
    protected static final String SQL_FIELD_MIDDLE_NAME = "middle_name";
    protected static final String SQL_FIELD_LAST_NAME = "last_name";
    protected static final String SQL_FIELD_DISPLAY_NAME = "display_name";
    protected static final String SQL_FIELD_BIRTHDAY = "birthday";
    protected static final String SQL_FIELD_BODY = "body";
    protected static final String SQL_FIELD_CATEGORIES = "categories";
    protected static final String SQL_FIELD_CHILDREN = "children";
    protected static final String SQL_FIELD_HOBBIES = "hobbies";
    protected static final String SQL_FIELD_GENDER = "gender";
    protected static final String SQL_FIELD_INITIALS = "initials";
    protected static final String SQL_FIELD_LANGUAGES = "languages";
    protected static final String SQL_FIELD_NICKNAME = "nickname";
    protected static final String SQL_FIELD_SPOUSE = "spouse";
    protected static final String SQL_FIELD_SUFFIX = "suffix";
    protected static final String SQL_FIELD_TITLE = "title";
    protected static final String SQL_FIELD_ASSISTANT = "assistant";
    protected static final String SQL_FIELD_COMPANY = "company";
    protected static final String SQL_FIELD_COMPANIES = "companies";
    protected static final String SQL_FIELD_DEPARTMENT = "department";
    protected static final String SQL_FIELD_JOB_TITLE = "job_title";
    protected static final String SQL_FIELD_MANAGER = "manager";
    protected static final String SQL_FIELD_MILEAGE = "mileage";
    protected static final String SQL_FIELD_OFFICE_LOCATION = "office_location";
    protected static final String SQL_FIELD_PROFESSION = "profession";
    protected static final String SQL_FIELD_TYPE = "type";
    protected static final String SQL_FIELD_VALUE = "value";
    protected static final String SQL_FIELD_STREET = "street";
    protected static final String SQL_FIELD_CITY = "city";
    protected static final String SQL_FIELD_STATE = "state";
    protected static final String SQL_FIELD_POSTAL_CODE = "postal_code";
    protected static final String SQL_FIELD_COUNTRY = "country";
    protected static final String SQL_FIELD_PO_BOX = "po_box";
    protected static final String SQL_FIELD_EXTENDED_ADDRESS = 
        "extended_address";

    protected static final int SQL_ANNIVERSARY_DIM = 16;
    protected static final int SQL_ASSISTANT_DIM = 128;
    protected static final int SQL_BIRTHDAY_DIM = 16;
    protected static final int SQL_CATEGORIES_DIM = 255;
    protected static final int SQL_CHILDREN_DIM = 255;
    protected static final int SQL_CITY_DIM = 64;
    protected static final int SQL_COMPANY_DIM = 255;
    protected static final int SQL_COMPANIES_DIM = 255;
    protected static final int SQL_COUNTRY_DIM = 32;
    protected static final int SQL_DEPARTMENT_DIM = 255;
    protected static final int SQL_DISPLAYNAME_DIM = 128;
    protected static final int SQL_EMAIL_DIM = 255;
    protected static final int SQL_FIRSTNAME_DIM = 64;
    protected static final int SQL_FOLDER_DIM = 255;
    protected static final int SQL_GENDER_DIM = 1;
    protected static final int SQL_HOBBIES_DIM = 255;
    protected static final int SQL_INITIALS_DIM = 16;
    protected static final int SQL_LABEL_DIM = 255; // @todo Enough?
    protected static final int SQL_LANGUAGES_DIM = 255;
    protected static final int SQL_LASTNAME_DIM = 64;
    protected static final int SQL_MANAGER_DIM = 128;
    protected static final int SQL_MIDDLENAME_DIM = 64;
    protected static final int SQL_MILEAGE_DIM = 16;
    protected static final int SQL_NICKNAME_DIM = 64;
    protected static final int SQL_NOTE_DIM = 4096;
    protected static final int SQL_OFFICELOCATION_DIM = 64;
    protected static final int SQL_PHONE_DIM = 255;
    protected static final int SQL_POSTALCODE_DIM = 16;
    protected static final int SQL_POSTALOFFICEADDRESS_DIM = 16;
    protected static final int SQL_EXTENDEDADDRESS_DIM = 255;
    protected static final int SQL_ROLE_DIM = 64;
    protected static final int SQL_SALUTATION_DIM = 32;
    protected static final int SQL_SPOUSE_DIM = 128;
    protected static final int SQL_STATE_DIM = 64;
    protected static final int SQL_STREET_DIM = 128;
    protected static final int SQL_SUBJECT_DIM = 255;
    protected static final int SQL_SUFFIX_DIM = 32;
    protected static final int SQL_TITLE_DIM = 128;
    protected static final int SQL_WEBPAGE_DIM = 255;

    /** Contact item type cannot be identified */
    protected static final int    TYPE_UNDEFINED = -1;
    protected static final int    TYPE_ASSISTANT_NUMBER  = 13;
    protected static final String FIELD_ASSISTANT_NUMBER =
            "AssistantTelephoneNumber";
    protected static final int    TYPE_BUSINESS_FAX_NUMBER  = 11;
    protected static final String FIELD_BUSINESS_FAX_NUMBER = "BusinessFaxNumber";
    protected static final int    TYPE_BUSINESS_TELEPHONE_NUMBER  = 10;
    protected static final String FIELD_BUSINESS_TELEPHONE_NUMBER =
            "BusinessTelephoneNumber";
    protected static final int    TYPE_CALLBACK_NUMBER  = 15;
    protected static final String FIELD_CALLBACK_NUMBER =
            "CallbackTelephoneNumber";
    protected static final int    TYPE_CAR_TELEPHONE_NUMBER  = 20;
    protected static final String FIELD_CAR_TELEPHONE_NUMBER =
            "CarTelephoneNumber";
    protected static final int    TYPE_COMPANY_MAIN_TELEPHONE_NUMBER  = 12;
    protected static final String FIELD_COMPANY_MAIN_TELEPHONE_NUMBER =
            "CompanyMainTelephoneNumber";
    protected static final int    TYPE_EMAIL_1_ADDRESS  = 4;
    protected static final String FIELD_EMAIL_1_ADDRESS = "Email1Address";
    protected static final int    TYPE_EMAIL_2_ADDRESS  = 16;
    protected static final String FIELD_EMAIL_2_ADDRESS = "Email2Address";
    protected static final int    TYPE_HOME_WEB_PAGE  = 6;
    protected static final String FIELD_HOME_WEB_PAGE = "HomeWebPage";
    protected static final int    TYPE_HOME_TELEPHONE_NUMBER  = 1;
    protected static final String FIELD_HOME_TELEPHONE_NUMBER =
            "HomeTelephoneNumber";
    protected static final int    TYPE_HOME_FAX_NUMBER  = 2;
    protected static final String FIELD_HOME_FAX_NUMBER = "HomeFaxNumber";
    protected static final int    TYPE_MOBILE_TELEPHONE_NUMBER  = 3;
    protected static final String FIELD_MOBILE_TELEPHONE_NUMBER =
            "MobileTelephoneNumber";
    protected static final int    TYPE_OTHER_TELEPHONE_NUMBER  = 30;
    protected static final String FIELD_OTHER_TELEPHONE_NUMBER =
            "OtherTelephoneNumber";
    protected static final int    TYPE_PAGER_NUMBER  = 14;
    protected static final String FIELD_PAGER_NUMBER = "PagerNumber";
    protected static final int    TYPE_PRIMARY_TELEPHONE_NUMBER  = 21;
    protected static final String FIELD_PRIMARY_TELEPHONE_NUMBER = "PrimaryTelephoneNumber";
    protected static final int    TYPE_WEB_PAGE  = 5;
    protected static final String FIELD_WEB_PAGE = "WebPage";
    protected static final int    TYPE_BUSINESS_WEB_PAGE  = 7;
    protected static final String FIELD_BUSINESS_WEB_PAGE = "BusinessWebPage";
    protected static final int    TYPE_INSTANT_MESSENGER  = 8;
    protected static final String FIELD_INSTANT_MESSENGER = "IMAddress";
    protected static final int    TYPE_BUSINESS_LABEL  = 17;
    protected static final String FIELD_BUSINESS_LABEL = "BusinessLabel";
    protected static final int    TYPE_HOME_LABEL  = 18;
    protected static final String FIELD_HOME_LABEL = "HomeLabel";
    protected static final int    TYPE_OTHER_LABEL  = 19;
    protected static final String FIELD_OTHER_LABEL = "OtherLabel";
    protected static final int    TYPE_HOME_2_TELEPHONE_NUMBER  = 22;
    protected static final String FIELD_HOME_2_TELEPHONE_NUMBER =
            "Home2TelephoneNumber";
    protected static final int    TYPE_EMAIL_3_ADDRESS  = 23;
    protected static final String FIELD_EMAIL_3_ADDRESS = "Email3Address";
    protected static final int    TYPE_BUSINESS_2_TELEPHONE_NUMBER  = 31;
    protected static final String FIELD_BUSINESS_2_TELEPHONE_NUMBER =
            "Business2TelephoneNumber";
    protected static final int    TYPE_OTHER_FAX_NUMBER  = 29;
    protected static final String FIELD_OTHER_FAX_NUMBER = "OtherFaxNumber";
    protected static final int    TYPE_TELEX_NUMBER  = 27;
    protected static final String FIELD_TELEX_NUMBER = "TelexNumber";
    protected static final int    TYPE_RADIO_TELEPHONE_NUMBER  = 28;
    protected static final String FIELD_RADIO_TELEPHONE_NUMBER =
            "RadioTelephoneNumber";

    protected static final String FIELD_JOB_TITLE = "JobTitle";
    protected static final String FIELD_NOTE = "Body";

    // Address type in fnbl_pim_address table
    protected static final int ADDRESS_TYPE_HOME  = 1;
    protected static final int ADDRESS_TYPE_WORK  = 2;
    protected static final int ADDRESS_TYPE_OTHER = 3;
    private static final String UNSET_FIELD_PLACEHOLDER = "<N/A>";

    private static final String SQL_GET_POTENTIAL_TWINS =
        new StringBuilder("SELECT c.id, i.type as item_type, i.value as item_value ")
        .append("FROM fnbl_pim_contact c LEFT OUTER JOIN fnbl_pim_contact_item i ")
        .append("ON (c.id = i.contact) WHERE (c.userid = ?) AND ( ")
        .append("(i.type IS null) OR i.type IN (")
        //all the contact different phone numbers
        .append(TYPE_ASSISTANT_NUMBER).append(",")
        .append(TYPE_BUSINESS_TELEPHONE_NUMBER).append(",")
        .append(TYPE_BUSINESS_FAX_NUMBER).append(",")
        .append(TYPE_BUSINESS_2_TELEPHONE_NUMBER).append(",")
        .append(TYPE_CALLBACK_NUMBER).append(",")
        .append(TYPE_CAR_TELEPHONE_NUMBER).append(",")
        .append(TYPE_COMPANY_MAIN_TELEPHONE_NUMBER).append(",")
        .append(TYPE_HOME_2_TELEPHONE_NUMBER).append(",")
        .append(TYPE_HOME_FAX_NUMBER).append(",")
        .append(TYPE_HOME_TELEPHONE_NUMBER).append(",")
        .append(TYPE_MOBILE_TELEPHONE_NUMBER).append(",")
        .append(TYPE_OTHER_FAX_NUMBER).append(",")
        .append(TYPE_OTHER_TELEPHONE_NUMBER).append(",")
        .append(TYPE_PAGER_NUMBER).append(",")
        .append(TYPE_PRIMARY_TELEPHONE_NUMBER).append(",")
        .append(TYPE_RADIO_TELEPHONE_NUMBER).append(",")
        .append(TYPE_TELEX_NUMBER).append(",")
        //all the contact different email address
        .append(TYPE_EMAIL_1_ADDRESS).append(",")
        .append(TYPE_EMAIL_2_ADDRESS).append(",")
        .append(TYPE_EMAIL_3_ADDRESS).append(") ) ").toString();

    private static final String SQL_UNNAMED_WHERE_CLAUSES = new StringBuilder()
        .append(" AND ( (c.first_name is null) OR (c.first_name = ?) )")
        .append(" AND ( (c.last_name is null) OR (c.last_name = ?) )")
        .append(" AND ( (c.company is null) OR (c.company = ?) )")
        .append(" AND ( (c.display_name is null) OR (c.display_name = ?) ) ")
        .toString();

    private static final String SQL_STATUS_NOT_D = " AND c.status != 'D' ";

    // ------------------------------------------------------------ Private data
    private Map<Long, Map<Integer, String>> unnamedContacts = null;

    //------------------------------------------------------------- Constructors

    /**
     * @param userId the user identifier
     * @see PIMEntityDAO#PIMEntityDAO(String, String)
     */
    public PIMContactDAO(String userId)  {
        super(userId, Def.ID_COUNTER);
        if (log.isTraceEnabled()) {
            log.trace("Created new PIMContactDAO for user ID " + userId);
        }
    }

    //----------------------------------------------------------- Public methods

    /**
     * Adds a contact. If necessary, a new ID is generated and set in the
     * ContactWrapper.
     *
     * @param cw as a ContactWrapper object, usually without an ID set.
     * @throws DAOException
     *
     * @see ContactWrapper
     */
    public void addItem(ContactWrapper cw) throws DAOException {
        if (log.isTraceEnabled()) {
            log.trace("Storing a contact item...");
        }

        Connection con = null;
        PreparedStatement ps = null;

        long id = 0;
        int type = 0;

        PersonalDetail personalDetail = null;
        BusinessDetail businessDetail = null;
        Address homeAddressBook = null;
        Address workAddressBook = null;
        Address otherAddressBook = null;

        Name name = null;
        Phone phone = null;
        Email email = null;
        WebPage webPage = null;

        List<WebPage> webPages = new ArrayList<WebPage>();
        List<Email> emails = new ArrayList<Email>();
        List<Phone> phones = new ArrayList<Phone>();
        List<String[]> labels = new ArrayList<String[]>();

        String webPageType = null;

        Short importance = null;
        Short sensitivity = null;
        String mileage = null;
        String subject = null;
        String folder = null;
        String anniversary = null;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        String displayName = null;
        String birthday = null;
        String categories = null;
        String gender = null;
        String hobbies = null;
        String initials = null;
        String languages = null;
        String nickName = null;
        String spouse = null;
        String suffix = null;
        String assistant = null;
        String officeLocation = null;
        String company = null;
        String companies = null;
        String department = null;
        String manager = null;
        String role = null;
        String children = null;
        String salutation = null;
        String sId = null;

        Timestamp lastUpdate = cw.getLastUpdate();
        if (lastUpdate == null) {
            lastUpdate = new Timestamp(System.currentTimeMillis());
        }

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            sId = cw.getId();
            if (sId == null) { // ...as it should be
                sId = getNextID();
                cw.setId(sId);
            }
            id = Long.parseLong(sId);

            Contact c = cw.getContact();
            personalDetail = c.getPersonalDetail();
            businessDetail = c.getBusinessDetail();
            name = c.getName();

            if (personalDetail != null) {
                homeAddressBook = personalDetail.getAddress();
                otherAddressBook = personalDetail.getOtherAddress();
                webPages.addAll(personalDetail.getWebPages());
                emails.addAll(personalDetail.getEmails());
                phones.addAll(personalDetail.getPhones());
            }

            if (businessDetail != null) {
                workAddressBook = businessDetail.getAddress();
                webPages.addAll(businessDetail.getWebPages());
                emails.addAll(businessDetail.getEmails());
                phones.addAll(businessDetail.getPhones());
                companies = businessDetail.getCompanies();

            }

            importance = c.getImportance();
            sensitivity = c.getSensitivity();
            mileage = c.getMileage();
            subject = c.getSubject();
            languages = c.getLanguages();

            categories = Property.stringFrom(c.getCategories());
            folder = c.getFolder();

            if (personalDetail != null) {
                anniversary = personalDetail.getAnniversary();
                birthday = personalDetail.getBirthday();
                children = personalDetail.getChildren();
                spouse = personalDetail.getSpouse();
                hobbies = personalDetail.getHobbies();
                gender = personalDetail.getGender();
            }

            if (businessDetail != null) {
                assistant = businessDetail.getAssistant();
                manager = businessDetail.getManager();
                officeLocation = businessDetail.getOfficeLocation();
                company = Property.stringFrom(businessDetail.getCompany());
                department = Property.stringFrom(businessDetail.getDepartment());
                role = Property.stringFrom(businessDetail.getRole());
            }

            if (name != null) {
                firstName = Property.stringFrom(name.getFirstName());
                middleName = Property.stringFrom(name.getMiddleName());
                lastName = Property.stringFrom(name.getLastName());
                displayName = Property.stringFrom(name.getDisplayName());
                initials = Property.stringFrom(name.getInitials());
                nickName = Property.stringFrom(name.getNickname());
                suffix = Property.stringFrom(name.getSuffix());
                salutation = Property.stringFrom(name.getSalutation());
            }

            ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT);

            //
            // GENERAL
            //

            if (log.isTraceEnabled()) {
                log.trace("Preparing statement with ID " + id);
            }
            ps.setLong(1, id);

            if (log.isTraceEnabled()) {
                log.trace("Preparing statement with user ID " + userId);
            }
            ps.setString(2, userId);

            ps.setLong(3, lastUpdate.getTime());
            ps.setString(4, String.valueOf(Def.PIM_STATE_NEW));

            boolean hasPhoto = false;
            Photo photo = personalDetail.getPhotoObject();
            if (photo != null && (photo.getImage() != null || photo.getUrl() != null)) {
                hasPhoto = true;
                ps.setShort(5, photo.getImage() != null ? ContactWrapper.PHOTO_IMAGE : ContactWrapper.PHOTO_URL);
            } else if (photo!= null) {
                ps.setShort(5, ContactWrapper.EMPTY_PHOTO);
            } else {
                ps.setNull(5, Types.SMALLINT);
            }

            //
            // CONTACT DETAILS
            //

            if (importance != null) {
                ps.setShort(6, importance.shortValue());
            } else {
                ps.setNull(6, Types.SMALLINT);
            }

            if (sensitivity != null) {
                ps.setShort(7, sensitivity.shortValue());
            } else {
                ps.setNull(7, Types.SMALLINT);
            }

            ps.setString(8, StringUtils.left(subject, SQL_SUBJECT_DIM));
            ps.setString(9, StringUtils.left(folder, SQL_FOLDER_DIM));

            //
            // PERSONAL DETAILS
            //

            ps.setString(10, StringUtils.left(anniversary, SQL_ANNIVERSARY_DIM));
            ps.setString(11, StringUtils.left(firstName, SQL_FIRSTNAME_DIM));
            ps.setString(12, StringUtils.left(middleName, SQL_MIDDLENAME_DIM));
            ps.setString(13, StringUtils.left(lastName, SQL_LASTNAME_DIM));
            ps.setString(14, StringUtils.left(displayName, SQL_DISPLAYNAME_DIM));
            ps.setString(15, StringUtils.left(birthday, SQL_BIRTHDAY_DIM));

            if (c.getNotes() != null && c.getNotes().size() > 0) {
                String noteValue =
                    ((Note) c.getNotes().get(0)).getPropertyValueAsString();
                ps.setString(16, StringUtils.left(noteValue, SQL_NOTE_DIM));
            } else {
                ps.setString(16, null);
            }

            ps.setString(17, StringUtils.left(categories, SQL_CATEGORIES_DIM));
            ps.setString(18, StringUtils.left(children, SQL_CHILDREN_DIM));
            ps.setString(19, StringUtils.left(hobbies, SQL_HOBBIES_DIM));
            ps.setString(20, StringUtils.left(initials, SQL_INITIALS_DIM));
            ps.setString(21, StringUtils.left(languages, SQL_LANGUAGES_DIM));
            ps.setString(22, StringUtils.left(nickName, SQL_NICKNAME_DIM));
            ps.setString(23, StringUtils.left(spouse, SQL_SPOUSE_DIM));
            ps.setString(24, StringUtils.left(suffix, SQL_SUFFIX_DIM));
            ps.setString(25, StringUtils.left(salutation, SQL_SALUTATION_DIM));

            //
            // BUSINESS DETAILS
            //
            ps.setString(26, StringUtils.left(assistant, SQL_ASSISTANT_DIM));
            ps.setString(27, StringUtils.left(company, SQL_COMPANY_DIM));
            ps.setString(28, StringUtils.left(department, SQL_DEPARTMENT_DIM));

            if (businessDetail.getTitles() != null
                    && businessDetail.getTitles().size() > 0) {
                String titleValue =
                    ((Title) businessDetail.getTitles().get(0)).getPropertyValueAsString();
                ps.setString(29, StringUtils.left(titleValue, SQL_TITLE_DIM));
            } else {
                ps.setString(29, null);
            }

            ps.setString(30, StringUtils.left(manager, SQL_MANAGER_DIM));
            if (mileage != null && mileage.length() > SQL_MILEAGE_DIM) {
                mileage = mileage.substring(0, SQL_MILEAGE_DIM);
            }
            ps.setString(31, StringUtils.left(mileage, SQL_MILEAGE_DIM));
            ps.setString(32, StringUtils.left(officeLocation, SQL_OFFICELOCATION_DIM));
            ps.setString(33, StringUtils.left(role, SQL_ROLE_DIM));
            ps.setString(34, StringUtils.left(companies, SQL_COMPANIES_DIM));
            ps.setString(35, StringUtils.left(gender, SQL_GENDER_DIM));

            ps.executeUpdate();

            DBTools.close(null, ps, null);

            //
            // emails
            //
            if (!emails.isEmpty()) {

                ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = emails.size(); i < l; i++) {

                    email = emails.get(i);

                    type = getContactEmailItemTypeFromEmailPropertyType(email.getEmailType());
                    // Unknown property: saves nothing
                    if (TYPE_UNDEFINED == type) continue;

                    String emailValue = email.getPropertyValueAsString();

                    if (emailValue != null && emailValue.length() != 0) {
                        if (emailValue.length() > SQL_EMAIL_DIM) {
                            emailValue = emailValue.substring(0, SQL_EMAIL_DIM);
                        }
                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, emailValue);

                        ps.executeUpdate();
                    }

                }

                DBTools.close(null, ps, null);

            }

            //
            // phones
            //
            if (!phones.isEmpty()) {

                ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = phones.size(); i < l; i++) {

                    phone = phones.get(i);

                    type = getContactPhoneItemTypeFromPhonePropertyType(phone.getPhoneType());
                    // Unknown property: saves nothing
                    if (TYPE_UNDEFINED == type) continue;

                    String phoneValue = phone.getPropertyValueAsString();
                    if (phoneValue != null && phoneValue.length() != 0) {
                        if (phoneValue.length() > SQL_PHONE_DIM) {
                            phoneValue = phoneValue.substring(0, SQL_PHONE_DIM);
                        }

                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, phoneValue);

                        ps.executeUpdate();
                    }

                }

                DBTools.close(null, ps, null);

            }

            //
            // webPages
            //
            if (!webPages.isEmpty()) {

                ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = webPages.size(); i < l; i++) {

                    webPage = webPages.get(i);

                    webPageType = webPage.getWebPageType();

                    if ((FIELD_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_WEB_PAGE;
                    } else if ((FIELD_HOME_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_HOME_WEB_PAGE;
                    } else if ((FIELD_BUSINESS_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_BUSINESS_WEB_PAGE;
                    } else {
                        //
                        // Unknown property: saves nothing
                        //
                        continue;
                    }

                    String webPageValue = webPage.getPropertyValueAsString();
                    if (webPageValue != null && webPageValue.length() != 0) {
                        if (webPageValue.length() > SQL_WEBPAGE_DIM) {
                            webPageValue = webPageValue.substring(0,
                                    SQL_WEBPAGE_DIM);
                        }

                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, webPageValue);

                        ps.executeUpdate();
                    }

                }

                DBTools.close(null, ps, null);

            }

            if (homeAddressBook != null) {

                String homeStreet = Property.stringFrom(homeAddressBook.getStreet());
                String homeCity = Property.stringFrom(homeAddressBook.getCity());
                String homePostalCode = Property.stringFrom(homeAddressBook.getPostalCode());
                String homeState = Property.stringFrom(homeAddressBook.getState());
                String homeCountry = Property.stringFrom(homeAddressBook.getCountry());
                String homePostalOfficeAddress =
                        Property.stringFrom(homeAddressBook.getPostOfficeAddress());
                String homeExtendedAddress =
                        Property.stringFrom(homeAddressBook.getExtendedAddress());

                String homeLabel = Property.stringFrom(homeAddressBook.getLabel());
                if (homeLabel != null) {
                    String[] label = {homeLabel, FIELD_HOME_LABEL};
                    labels.add(label);
                }

                String[] homeAddressFields = {homeStreet, homeCity,
                homePostalCode, homeCountry, homeState,
                homePostalOfficeAddress, homeExtendedAddress};

                if(!hasOnlyEmptyOrNullContent(homeAddressFields)){

                    ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                    ps.setLong(1, id);
                    ps.setInt(2, ADDRESS_TYPE_HOME);
                    ps.setString(3,
                            replaceNewLine(
                            StringUtils.left(homeStreet, SQL_STREET_DIM)));
                    ps.setString(4, StringUtils.left(homeCity, SQL_CITY_DIM));
                    ps.setString(5, StringUtils.left(homeState, SQL_STATE_DIM));
                    ps.setString(6,
                            StringUtils.left(homePostalCode, SQL_POSTALCODE_DIM));
                    ps.setString(7, StringUtils.left(homeCountry, SQL_COUNTRY_DIM));
                    ps.setString(8,
                            StringUtils.left(homePostalOfficeAddress,
                            SQL_POSTALOFFICEADDRESS_DIM));
                    ps.setString(9,
                            StringUtils.left(homeExtendedAddress,
                            SQL_EXTENDEDADDRESS_DIM));

                    ps.executeUpdate();

                    DBTools.close(null, ps, null);
                }
            }

            if (otherAddressBook != null) {

                String otherStreet = Property.stringFrom(otherAddressBook.getStreet());
                String otherCity = Property.stringFrom(otherAddressBook.getCity());
                String otherPostalCode = Property.stringFrom(otherAddressBook.getPostalCode());
                String otherState = Property.stringFrom(otherAddressBook.getState());
                String otherCountry = Property.stringFrom(otherAddressBook.getCountry());
                String otherPostalOfficeAddress =
                        Property.stringFrom(otherAddressBook.getPostOfficeAddress());
                String otherExtendedAddress =
                        Property.stringFrom(otherAddressBook.getExtendedAddress());

                String otherLabel = Property.stringFrom(otherAddressBook.getLabel());
                if (otherLabel != null) {
                    String[] label = {otherLabel, FIELD_OTHER_LABEL};
                    labels.add(label);
                }

                String[] otherAddressFields = {otherStreet, otherCity,
                otherPostalCode, otherCountry, otherState,
                otherPostalOfficeAddress, otherExtendedAddress};

                if (!hasOnlyEmptyOrNullContent(otherAddressFields)){

                    ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                    ps.setLong(1, id);
                    ps.setInt(2, ADDRESS_TYPE_OTHER);
                    ps.setString(3,
                            replaceNewLine(
                            StringUtils.left(otherStreet, SQL_STREET_DIM)));
                    ps.setString(4, StringUtils.left(otherCity, SQL_CITY_DIM));
                    ps.setString(5, StringUtils.left(otherState, SQL_STATE_DIM));
                    ps.setString(6,
                            StringUtils.left(otherPostalCode, SQL_POSTALCODE_DIM));
                    ps.setString(7, StringUtils.left(otherCountry, SQL_COUNTRY_DIM));
                    ps.setString(8,
                            StringUtils.left(otherPostalOfficeAddress,
                            SQL_POSTALOFFICEADDRESS_DIM));
                    ps.setString(9,
                            StringUtils.left(otherExtendedAddress,
                            SQL_EXTENDEDADDRESS_DIM));

                    ps.executeUpdate();

                    DBTools.close(null, ps, null);

                }
            }

            if (workAddressBook != null) {

                String workStreet = Property.stringFrom(workAddressBook.getStreet());
                String workCity = Property.stringFrom(workAddressBook.getCity());
                String workPostalCode = Property.stringFrom(workAddressBook.getPostalCode());
                String workState = Property.stringFrom(workAddressBook.getState());
                String workCountry = Property.stringFrom(workAddressBook.getCountry());
                String workPostalOfficeAddress =
                        Property.stringFrom(workAddressBook.getPostOfficeAddress());
                String workExtendedAddress =
                        Property.stringFrom(workAddressBook.getExtendedAddress());

                String workLabel = Property.stringFrom(workAddressBook.getLabel());
                if (workLabel != null) {
                    String[] label = {workLabel, FIELD_BUSINESS_LABEL};
                    labels.add(label);
                }

                String[] workAddressFields = {workStreet, workCity,
                workPostalCode, workCountry, workState,
                workPostalOfficeAddress, workExtendedAddress};

                if (!hasOnlyEmptyOrNullContent(workAddressFields)){

                    ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                    ps.setLong(1, id);
                    ps.setInt(2, ADDRESS_TYPE_WORK);
                    ps.setString(3,
                            replaceNewLine(
                            StringUtils.left(workStreet, SQL_STREET_DIM)));
                    ps.setString(4, StringUtils.left(workCity, SQL_CITY_DIM));
                    ps.setString(5, StringUtils.left(workState, SQL_STATE_DIM));
                    ps.setString(6,
                            StringUtils.left(workPostalCode, SQL_POSTALCODE_DIM));
                    ps.setString(7, StringUtils.left(workCountry, SQL_COUNTRY_DIM));
                    ps.setString(8,
                            StringUtils.left(workPostalOfficeAddress,
                            SQL_POSTALOFFICEADDRESS_DIM));
                    ps.setString(9,
                            StringUtils.left(workExtendedAddress,
                            SQL_EXTENDEDADDRESS_DIM));

                    ps.executeUpdate();

                    DBTools.close(null, ps, null);
                }

            }

            //
            // labels
            //
            if (!labels.isEmpty()) {

                ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = labels.size(); i < l; i++) {

                    String[] label = labels.get(i);

                    String labelType = label[1];

                    if ((FIELD_HOME_LABEL).equals(labelType)) {
                        type = TYPE_HOME_LABEL;
                    } else if ((FIELD_BUSINESS_LABEL).equals(labelType)) {
                        type = TYPE_BUSINESS_LABEL;
                    } else if ((FIELD_OTHER_LABEL).equals(labelType)) {
                        type = TYPE_OTHER_LABEL;
                    } else {
                        //
                        // Unknown property: saves nothing
                        //
                        continue;
                    }

                    String labelValue = label[0];
                    if (labelValue != null && labelValue.length() != 0) {
                        if (labelValue.length() > SQL_LABEL_DIM) {
                            labelValue = labelValue.substring(0,
                                    SQL_LABEL_DIM);
                        }

                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, labelValue);

                        ps.executeUpdate();
                    }

                }

                DBTools.close(null, ps, null);

            }

            if (hasPhoto) {
                insertPhoto(con, Long.parseLong(cw.getId()), photo);
            }

        } catch (Exception e) {
            throw new DAOException("Error adding contact.", e);
        } finally {
            DBTools.close(con, ps, null);
        }

        if(log.isTraceEnabled()) {
            log.trace("Added item with ID '" + id + "'");
        }
    }

    /**
     * Updates a contact.
     *
     * @param cw as a ContactWrapper object. If its last update time is null,
     *           then it's set to the current time.
     * @return the UID of the contact
     * @throws DAOException
     *
     * @see ContactWrapper
     */
    public String updateItem(ContactWrapper cw) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        int type = 0;

        PersonalDetail personalDetail = null;
        BusinessDetail businessDetail = null;
        Address homeAddressBook = null;
        Address workAddressBook = null;
        Address otherAddressBook = null;

        Name name = null;
        Phone phone = null;
        Email email = null;
        WebPage webPage = null;

        List<WebPage> webPages = new ArrayList<WebPage>();
        List<Email> emails = new ArrayList<Email>();
        List<Phone> phones = new ArrayList<Phone>();
        List<String[]> labels = new ArrayList<String[]>();

        String phoneType = null;
        String webPageType = null;

        StringBuffer queryUpdateFunPimContact = null;

        Short importance = null;
        Short sensitivity = null;
        String mileage = null;
        String subject = null;
        String folder = null;
        String anniversary = null;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        String displayName = null;
        String birthday = null;
        String note = null;
        String categories = null;
        String hobbies = null;
        String gender = null;
        String initials = null;
        String languages = null;
        String nickName = null;
        String spouse = null;
        String suffix = null;
        String assistant = null;
        String company = null;
        String companies = null;
        String department = null;
        String jobTitle = null;
        String manager = null;
        String city = null;
        String state = null;
        String role = null;
        String children = null;
        String salutation = null;
        String officeLocation = null;
        String street = null;
        String postalCode = null;
        String country = null;
        String postOfficeAddress = null;
        String extendedAddress = null;

        String[] addressFields = null;

        boolean findRecord = false;
        boolean emptyAddress = false;

        short   photoType        = ContactWrapper.EMPTY_PHOTO;
        boolean photoToRemove    = false;
        boolean photoToSet       = false;
        boolean photoNothingToDo = false;

        StringBuffer sqlUpdateFunPimAddress = null;

        try {

            Timestamp lastUpdate =
                    (cw.getLastUpdate() == null) ?
                        new Timestamp(System.currentTimeMillis())
                        : cw.getLastUpdate();

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            Contact c = cw.getContact();

            personalDetail = c.getPersonalDetail();
            businessDetail = c.getBusinessDetail();
            name = c.getName();
            importance = c.getImportance();
            sensitivity = c.getSensitivity();
            mileage = c.getMileage();
            subject = c.getSubject();
            languages = c.getLanguages();
            folder = c.getFolder();
            categories = Property.stringFrom(c.getCategories());

            if (personalDetail != null) {

                homeAddressBook = personalDetail.getAddress();
                otherAddressBook = personalDetail.getOtherAddress();
                anniversary = personalDetail.getAnniversary();
                birthday = personalDetail.getBirthday();
                children = personalDetail.getChildren();
                spouse = personalDetail.getSpouse();
                hobbies = personalDetail.getHobbies();
                gender = personalDetail.getGender();
                webPages.addAll(personalDetail.getWebPages());
                emails.addAll(personalDetail.getEmails());
                phones.addAll(personalDetail.getPhones());
            }

            if (businessDetail != null) {

                assistant = businessDetail.getAssistant();
                manager = businessDetail.getManager();
                workAddressBook = businessDetail.getAddress();
                companies = businessDetail.getCompanies();
                company = Property.stringFrom(businessDetail.getCompany());
                department = Property.stringFrom(businessDetail.getDepartment());
                role = Property.stringFrom(businessDetail.getRole());
                officeLocation = businessDetail.getOfficeLocation();
                webPages.addAll(businessDetail.getWebPages());
                emails.addAll(businessDetail.getEmails());
                phones.addAll(businessDetail.getPhones());
            }

            if (name != null) {
                firstName = Property.stringFrom(name.getFirstName());
                middleName = Property.stringFrom(name.getMiddleName());
                lastName = Property.stringFrom(name.getLastName());
                displayName = Property.stringFrom(name.getDisplayName());
                initials = Property.stringFrom(name.getInitials());
                nickName = Property.stringFrom(name.getNickname());
                suffix = Property.stringFrom(name.getSuffix());
                salutation = Property.stringFrom(name.getSalutation());
            }

            if (c.getNotes() != null && c.getNotes().size() > 0) {
                note = ((Note) c.getNotes().get(0)).getPropertyValueAsString();
            } else {
                note = null;
            }

            if (businessDetail.getTitles() != null
                    && businessDetail.getTitles().size() > 0) {
                jobTitle = ((Title) businessDetail.getTitles().get(0))
                .getPropertyValueAsString();
            } else {
                jobTitle = null;
            }

            queryUpdateFunPimContact = new StringBuffer();

            queryUpdateFunPimContact
                    .append(SQL_UPDATE_FNBL_PIM_CONTACT_BEGIN
                    + SQL_FIELD_LAST_UPDATE + SQL_EQUALS_QUESTIONMARK_COMMA);

            //
            // Updating photo:
            // 1. if the contact doesn't have a photo (photo null),
            //    nothing should be done (If there is a photo in the db this will
            //    be kept)
            // 2. if the contact has a photo (image or url) it must be inserted
            //    in the db
            // 3. if the photo has a photo but the image and the url are null,
            //    the one in the db must be removed
            //
            Photo photo = personalDetail.getPhotoObject();
            if (photo == null) {
                //
                // nothing to do
                //
                photoNothingToDo = true;
            } else {
                if (photo.getImage() != null) {
                    photoType  = ContactWrapper.PHOTO_IMAGE;
                    photoToSet = true;
                } else if (photo.getUrl() != null) {
                    photoType  = ContactWrapper.PHOTO_URL;
                    photoToSet = true;
                } else {
                    photoToRemove = true;
                    photoType  = ContactWrapper.EMPTY_PHOTO;
                }
                queryUpdateFunPimContact.append(SQL_FIELD_PHOTO_TYPE)
                                        .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (importance != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_IMPORTANCE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (sensitivity != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_SENSITIVITY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (subject != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_SUBJECT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (folder != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_FOLDER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (anniversary != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_ANNIVERSARY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (firstName != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_FIRST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (middleName != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_MIDDLE_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (lastName != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_LAST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (displayName != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_DISPLAY_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (birthday != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_BIRTHDAY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (note != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_BODY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (categories != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_CATEGORIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (children != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_CHILDREN
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (hobbies != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_HOBBIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (initials != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_INITIALS
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (languages != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_LANGUAGES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (nickName != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_NICKNAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (spouse != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_SPOUSE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (suffix != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_SUFFIX
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (salutation != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_TITLE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (assistant != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_ASSISTANT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (company != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_COMPANY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (department != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_DEPARTMENT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (jobTitle != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_JOB_TITLE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (manager != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_MANAGER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (mileage != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_MILEAGE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (officeLocation != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_OFFICE_LOCATION
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (role != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_PROFESSION
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (companies != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_COMPANIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            if (gender != null) {
                queryUpdateFunPimContact.append(SQL_FIELD_GENDER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimContact
                    .append(SQL_FIELD_STATUS + SQL_EQUALS_QUESTIONMARK
                    + SQL_UPDATE_FNBL_PIM_CONTACT_END);

            ps = con.prepareStatement(queryUpdateFunPimContact.toString());

            int k = 1;

            //
            // GENERAL
            //
            ps.setLong(k++, lastUpdate.getTime());

            //
            // PHOTO TYPE
            //
            if (!photoNothingToDo) {
                ps.setShort(k++, photoType);
            }

            //
            // CONTACT DETAILS
            //
            if (importance != null) {
                ps.setShort(k++, importance.shortValue());
            }
            if (sensitivity != null) {
                ps.setShort(k++, sensitivity.shortValue());
            }

            if (subject != null) {
                if (subject.length() > SQL_SUBJECT_DIM) {
                    subject = subject.substring(0, SQL_SUBJECT_DIM);
                }
                ps.setString(k++, subject);
            }
            //
            // folder
            //
            if (folder != null) {
                if (folder.length() > SQL_FOLDER_DIM) {
                    folder = folder.substring(0, SQL_FOLDER_DIM);
                }
                ps.setString(k++, folder);
            }

            //
            // PERSONAL DETAILS
            //

            //
            // anniversary
            //
            if (anniversary != null) {
                if (anniversary.length() > SQL_ANNIVERSARY_DIM) {
                    anniversary = anniversary.substring(0, SQL_ANNIVERSARY_DIM);
                }
                ps.setString(k++, anniversary);
            }
            //
            // firstName
            //
            if (firstName != null) {
                if (firstName.length() > SQL_FIRSTNAME_DIM) {
                    firstName = firstName.substring(0, SQL_FIRSTNAME_DIM);
                }
                ps.setString(k++, firstName);
            }
            //
            // middleName
            //
            if (middleName != null) {
                if (middleName.length() > SQL_MIDDLENAME_DIM) {
                    middleName = middleName.substring(0, SQL_MIDDLENAME_DIM);
                }
                ps.setString(k++, middleName);
            }
            //
            // lastName
            //
            if (lastName != null) {
                if (lastName.length() > SQL_LASTNAME_DIM) {
                    lastName = lastName.substring(0, SQL_LASTNAME_DIM);
                }
                ps.setString(k++, lastName);
            }
            //
            // displayName
            //
            if (displayName != null) {
                if (displayName.length() > SQL_DISPLAYNAME_DIM) {
                    displayName = displayName.substring(0, SQL_DISPLAYNAME_DIM);
                }
                ps.setString(k++, displayName);
            }
            //
            // birthday
            //
            if (birthday != null) {
                if (birthday.length() > SQL_BIRTHDAY_DIM) {
                    birthday = birthday.substring(0, SQL_BIRTHDAY_DIM);
                }
                ps.setString(k++, birthday);
            }
            //
            // note
            //
            if (note != null) {
                if (note.length() > SQL_NOTE_DIM) {
                    note = note.substring(0, SQL_NOTE_DIM);
                }
                ps.setString(k++, note);
            }
            //
            // categories
            //
            if (categories != null) {
                if (categories.length() > SQL_CATEGORIES_DIM) {
                    categories = categories.substring(0, SQL_CATEGORIES_DIM);
                }
                ps.setString(k++, categories);
            }
            //
            // children
            //
            if (children != null) {
                if (children.length() > SQL_CHILDREN_DIM) {
                    children = children.substring(0, SQL_CHILDREN_DIM);
                }
                ps.setString(k++, children);
            }
            //
            // hobbies
            //
            if (hobbies != null) {
                if (hobbies.length() > SQL_HOBBIES_DIM) {
                    hobbies = hobbies.substring(0, SQL_HOBBIES_DIM);
                }
                ps.setString(k++, hobbies);
            }
            //
            // initials
            //
            if (initials != null) {
                if (initials.length() > SQL_INITIALS_DIM) {
                    initials = initials.substring(0, SQL_INITIALS_DIM);
                }
                ps.setString(k++, initials);
            }
            //
            // languages
            //
            if (languages != null) {
                if (languages.length() > SQL_LANGUAGES_DIM) {
                    languages = initials.substring(0, SQL_LANGUAGES_DIM);
                }
                ps.setString(k++, languages);
            }
            //
            // nickName
            //
            if (nickName != null) {
                if (nickName.length() > SQL_NICKNAME_DIM) {
                    nickName = nickName.substring(0, SQL_NICKNAME_DIM);
                }
                ps.setString(k++, nickName);
            }
            //
            // spouse
            //
            if (spouse != null) {
                if (spouse.length() > SQL_SPOUSE_DIM) {
                    spouse = spouse.substring(0, SQL_SPOUSE_DIM);
                }
                ps.setString(k++, spouse);
            }
            //
            // suffix
            //
            if (suffix != null) {
                if (suffix.length() > SQL_SUFFIX_DIM) {
                    suffix = suffix.substring(0, SQL_SUFFIX_DIM);
                }
                ps.setString(k++, suffix);
            }
            //
            // salutation
            //
            if (salutation != null) {
                if (salutation.length() > SQL_SALUTATION_DIM) {
                    salutation = salutation.substring(0, SQL_SALUTATION_DIM);
                }
                ps.setString(k++, salutation);
            }
            //
            // assistant
            //
            if (assistant != null) {
                if (assistant.length() > SQL_ASSISTANT_DIM) {
                    assistant = assistant.substring(0, SQL_ASSISTANT_DIM);
                }
                ps.setString(k++, assistant);
            }
            //
            // company
            //
            if (company != null) {
                if (company.length() > SQL_COMPANY_DIM) {
                    company = company.substring(0, SQL_COMPANY_DIM);
                }
                ps.setString(k++, company);
            }
            //
            // department
            //
            if (department != null) {
                if (department.length() > SQL_DEPARTMENT_DIM) {
                    department = department.substring(0, SQL_DEPARTMENT_DIM);
                }
                ps.setString(k++, department);
            }
            //
            // jobTitle
            //
            if (jobTitle != null) {
                if (jobTitle.length() > SQL_TITLE_DIM) {
                    jobTitle = jobTitle.substring(0, SQL_TITLE_DIM);
                }
                ps.setString(k++, jobTitle);
            }
            //
            // manager
            //
            if (manager != null) {
                if (manager.length() > SQL_MANAGER_DIM) {
                    manager = manager.substring(0, SQL_MANAGER_DIM);
                }
                ps.setString(k++, manager);
            }
            //
            // mileage
            //
            if (mileage != null) {
                if (mileage.length() > SQL_MILEAGE_DIM) {
                    mileage = mileage.substring(0, SQL_MILEAGE_DIM);
                }
                ps.setString(k++, mileage);
            }
            if (officeLocation != null) {
                if (officeLocation.length() > SQL_OFFICELOCATION_DIM) {
                    officeLocation = officeLocation.substring(0,
                            SQL_OFFICELOCATION_DIM);
                }
                ps.setString(k++, officeLocation);
            }
            //
            // role
            //
            if (role != null) {
                if (role.length() > SQL_ROLE_DIM) {
                    role = role.substring(0, SQL_ROLE_DIM);
                }
                ps.setString(k++, role);
            }

            //
            // companies
            //
            if (companies != null) {
                if (companies.length() > SQL_COMPANIES_DIM) {
                    companies = companies.substring(0, SQL_COMPANIES_DIM);
                }
                ps.setString(k++, companies);
            }

            //
            // gender
            //
            if (gender != null) {
                if (gender.length() > SQL_GENDER_DIM) {
                    gender = gender.substring(0, SQL_GENDER_DIM);
                }
                ps.setString(k++, gender);
            }

            //
            // status
            //
            ps.setString(k++, String.valueOf('U'));
            //
            // id
            //
            ps.setLong(k++, Long.parseLong(cw.getId()));
            //
            // userId
            //
            ps.setString(k++, userId);

            ps.executeUpdate();

            DBTools.close(null, ps, null);

            //
            // emails
            //
            if (!emails.isEmpty()) {
                ps1 = con
                        .prepareStatement(SQL_CHECK_IF_IN_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = emails.size(); i < l; i++) {

                    email = emails.get(i);

                   if ((FIELD_EMAIL_1_ADDRESS).equals(email.getEmailType())) {
                        type = TYPE_EMAIL_1_ADDRESS;
                    } else if ((FIELD_EMAIL_2_ADDRESS).equals(email
                            .getEmailType())) {
                        type = TYPE_EMAIL_2_ADDRESS;
                    } else if ((FIELD_EMAIL_3_ADDRESS).equals(email
                            .getEmailType())) {
                        type = TYPE_EMAIL_3_ADDRESS;
                    } else if ((FIELD_INSTANT_MESSENGER).equals(email
                            .getEmailType())) {
                        type = TYPE_INSTANT_MESSENGER;
                    } else {
                        //
                        // no save unknown property
                        //
                        continue;
                    }

                    ps1.setLong(1, Long.parseLong(cw.getId()));
                    ps1.setInt(2, type);

                    rs = ps1.executeQuery();

                    findRecord = rs.next();

                    rs.close();
                    rs = null;

                    String emailValue = email.getPropertyValueAsString();

                    emailValue = StringUtils.left(emailValue, SQL_EMAIL_DIM);

                    if (!findRecord) {

                        if (emailValue != null && emailValue.length() != 0) {
                            ps = con.prepareStatement(
                                SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                            ps.setString(3, emailValue);

                            ps.executeUpdate();

                            DBTools.close(null, ps, null);
                        }

                    } else {

                        if (emailValue != null) {
                            ps = con.prepareStatement(
                                SQL_UPDATE_FNBL_PIM_CONTACT_ITEM);

                            ps.setString(1, emailValue);
                            ps.setLong(2, Long.parseLong(cw.getId()));
                            ps.setInt(3, type);

                        } else {

                            ps = con.prepareStatement(
                                SQL_DELETE_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);

                        }

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    }
                }

                DBTools.close(null, ps1, null);

            }

            //
            // phones
            //
            if (!phones.isEmpty()) {

                ps1 = con.prepareStatement(
                    SQL_CHECK_IF_IN_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = phones.size(); i < l; i++) {

                    phone = phones.get(i);

                    phoneType = phone.getPhoneType();

                     if ((FIELD_HOME_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_HOME_TELEPHONE_NUMBER;
                    } else if ((FIELD_HOME_2_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_HOME_2_TELEPHONE_NUMBER;
                    } else if ((FIELD_HOME_FAX_NUMBER).equals(phoneType)) {
                        type = TYPE_HOME_FAX_NUMBER;
                    } else if ((FIELD_MOBILE_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_MOBILE_TELEPHONE_NUMBER;
                    } else if ((FIELD_CAR_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_CAR_TELEPHONE_NUMBER;
                    } else if ((FIELD_OTHER_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_OTHER_TELEPHONE_NUMBER;
                    } else if ((FIELD_OTHER_FAX_NUMBER).equals(phoneType)) {
                        type = TYPE_OTHER_FAX_NUMBER;
                    } else if ((FIELD_PRIMARY_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_PRIMARY_TELEPHONE_NUMBER;
                    } else if ((FIELD_BUSINESS_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_BUSINESS_TELEPHONE_NUMBER;
                    } else if ((FIELD_BUSINESS_2_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_BUSINESS_2_TELEPHONE_NUMBER;
                    } else if ((FIELD_BUSINESS_FAX_NUMBER).equals(phoneType)) {
                        type = TYPE_BUSINESS_FAX_NUMBER;
                    } else if ((FIELD_COMPANY_MAIN_TELEPHONE_NUMBER).equals(phoneType)) {
                        type = TYPE_COMPANY_MAIN_TELEPHONE_NUMBER;
                    } else if ((FIELD_PAGER_NUMBER).equals(phoneType)) {
                        type = TYPE_PAGER_NUMBER;
                    } else if ((FIELD_ASSISTANT_NUMBER).equals(phoneType)) {
                        type = TYPE_ASSISTANT_NUMBER;
                    } else if ((FIELD_CALLBACK_NUMBER).equals(phoneType)) {
                        type = TYPE_CALLBACK_NUMBER;
                    } else {
                        //
                        // Unknown property: saves nothing
                        //
                        continue;
                    }

                    ps1.setLong(1, Long.parseLong(cw.getId()));
                    ps1.setInt(2, type);

                    rs = ps1.executeQuery();

                    findRecord = rs.next();

                    String phoneValue = phone.getPropertyValueAsString();
                    phoneValue = StringUtils.left(phoneValue, SQL_EMAIL_DIM);

                    if (!findRecord) {
                        if (phoneValue != null && phoneValue.length() != 0) {
                            ps = con.prepareStatement(
                                SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                            ps.setString(3, phoneValue);

                            ps.executeUpdate();

                            DBTools.close(null, ps, null);
                        }

                    } else {

                        if (phoneValue != null){
                            ps = con.prepareStatement(
                                SQL_UPDATE_FNBL_PIM_CONTACT_ITEM);

                            ps.setString(1, phoneValue);
                            ps.setLong(2, Long.parseLong(cw.getId()));
                            ps.setInt(3, type);

                        } else {
                            ps = con.prepareStatement(
                                SQL_DELETE_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                        }

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    }

                    DBTools.close(null, null, rs);

                }

                DBTools.close(null, ps1, null);

            }

            //
            // web pages
            //
            if (!webPages.isEmpty()) {

                ps1 = con.prepareStatement(
                    SQL_CHECK_IF_IN_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = webPages.size(); i < l; i++) {

                    webPage = webPages.get(i);

                    webPageType = webPage.getWebPageType();

                     if ((FIELD_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_WEB_PAGE;
                    } else if ((FIELD_HOME_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_HOME_WEB_PAGE;
                    } else if ((FIELD_BUSINESS_WEB_PAGE).equals(webPageType)) {
                        type = TYPE_BUSINESS_WEB_PAGE;
                    } else {
                        //
                        // Unknown property: saves nothing
                        //
                        continue;
                    }

                    ps1.setLong(1, Long.parseLong(cw.getId()));
                    ps1.setInt(2, type);

                    rs = ps1.executeQuery();

                    findRecord = rs.next();

                    String webPageValue = webPage.getPropertyValueAsString();
                    webPageValue = StringUtils.left(webPageValue, SQL_WEBPAGE_DIM);

                    if (!findRecord) {

                        if (webPageValue != null
                                && webPageValue.length() != 0) {
                            ps = con.prepareStatement(
                                SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                            ps.setString(3, webPageValue);

                            ps.executeUpdate();

                            DBTools.close(null, ps, null);
                        }

                    } else {

                        if (webPageValue != null){
                            ps = con.prepareStatement(
                                SQL_UPDATE_FNBL_PIM_CONTACT_ITEM);

                            ps.setString(1, webPageValue);
                            ps.setLong(2, Long.parseLong(cw.getId()));
                            ps.setInt(3, type);
                        } else {
                            ps = con.prepareStatement(
                                SQL_DELETE_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                        }

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    }

                    DBTools.close(null, null, rs);

                }

                DBTools.close(null, ps1, null);

            }

            //
            // home address
            //
            if (homeAddressBook != null) {

                ps = con.prepareStatement(SQL_SELECT_FROM_FNBL_PIM_ADDRESS);

                ps.setLong(1, Long.parseLong(cw.getId()));
                ps.setInt(2, ADDRESS_TYPE_HOME);

                rs = ps.executeQuery();

                findRecord = rs.next();

                DBTools.close(null, ps, rs);

                street = Property.stringFrom(homeAddressBook.getStreet());
                city = Property.stringFrom(homeAddressBook.getCity());
                postalCode = Property.stringFrom(homeAddressBook.getPostalCode());
                state = Property.stringFrom(homeAddressBook.getState());
                country = Property.stringFrom(homeAddressBook.getCountry());
                postOfficeAddress =
                    Property.stringFrom(homeAddressBook.getPostOfficeAddress());
                extendedAddress =
                    Property.stringFrom(homeAddressBook.getExtendedAddress());

                street = replaceNewLine(StringUtils.left(street, SQL_STREET_DIM));
                city = StringUtils.left(city, SQL_CITY_DIM);
                state = StringUtils.left(state, SQL_STATE_DIM);
                postalCode = StringUtils.left(postalCode, SQL_POSTALCODE_DIM);
                country = StringUtils.left(country, SQL_COUNTRY_DIM);
                postOfficeAddress = StringUtils.left(postOfficeAddress,
                        SQL_POSTALOFFICEADDRESS_DIM);
                extendedAddress = StringUtils.left(extendedAddress,
                        SQL_EXTENDEDADDRESS_DIM);

                String homeLabel = Property.stringFrom(homeAddressBook.getLabel());
                if (homeLabel != null) {
                    String[] label = {homeLabel, FIELD_HOME_LABEL};
                    labels.add(label);
                }

                addressFields = new String[] {street, city, postalCode,
                country, state, postOfficeAddress, extendedAddress};

                emptyAddress = hasOnlyNullContent(addressFields);

                if (!emptyAddress) {

                    if(!findRecord && !hasOnlyEmptyOrNullContent(addressFields)){
                        ps = con.prepareStatement(
                            SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                        ps.setLong(1, Long.parseLong(cw.getId()));
                        ps.setInt(2, ADDRESS_TYPE_HOME);
                        ps.setString(3, street);
                        ps.setString(4, city);
                        ps.setString(5, state);
                        ps.setString(6, postalCode);
                        ps.setString(7, country);
                        ps.setString(8, postOfficeAddress);
                        ps.setString(9, extendedAddress);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);

                    } else {

                        sqlUpdateFunPimAddress = new StringBuffer();

                        sqlUpdateFunPimAddress
                                .append(SQL_UPDATE_FNBL_PIM_ADDRESS_BEGIN);

                        if (street != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STREET
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (city != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_CITY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (state != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STATE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postalCode != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_POSTAL_CODE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (country != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_COUNTRY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postOfficeAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_PO_BOX
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (extendedAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_EXTENDED_ADDRESS
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }

                        sqlUpdateFunPimAddress
                                .append(SQL_FIELD_TYPE + SQL_EQUALS_QUESTIONMARK
                                + SQL_UPDATE_FNBL_PIM_ADDRESS_END);

                        ps = con.prepareStatement(
                            sqlUpdateFunPimAddress.toString());

                        k = 1;

                        if (street != null) {
                            ps.setString(k++, street);
                        }
                        if (city != null) {
                            ps.setString(k++, city);
                        }
                        if (state != null) {
                            ps.setString(k++, state);
                        }
                        if (postalCode != null) {
                            ps.setString(k++, postalCode);
                        }
                        if (country != null) {
                            ps.setString(k++, country);
                        }
                        if (postOfficeAddress != null) {
                            ps.setString(k++, postOfficeAddress);
                        }
                        if (extendedAddress != null) {
                            ps.setString(k++, extendedAddress);
                        }

                        ps.setInt(k++, ADDRESS_TYPE_HOME);
                        ps.setLong(k++, Long.parseLong(cw.getId()));
                        ps.setInt(k++, ADDRESS_TYPE_HOME);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    }

                }

            }

            //
            // other address
            //
            if (otherAddressBook != null) {

                ps = con.prepareStatement(SQL_SELECT_FROM_FNBL_PIM_ADDRESS);

                ps.setLong(1, Long.parseLong(cw.getId()));
                ps.setInt(2, ADDRESS_TYPE_OTHER);

                rs = ps.executeQuery();

                findRecord = rs.next();

                DBTools.close(null, ps, rs);

                street = Property.stringFrom(otherAddressBook.getStreet());
                city = Property.stringFrom(otherAddressBook.getCity());
                postalCode = Property.stringFrom(otherAddressBook.getPostalCode());
                state = Property.stringFrom(otherAddressBook.getState());
                country = Property.stringFrom(otherAddressBook.getCountry());
                postOfficeAddress =
                    Property.stringFrom(otherAddressBook.getPostOfficeAddress());
                extendedAddress =
                    Property.stringFrom(otherAddressBook.getExtendedAddress());

                street = replaceNewLine(StringUtils.left(street, SQL_STREET_DIM));
                city = StringUtils.left(city, SQL_CITY_DIM);
                state = StringUtils.left(state, SQL_STATE_DIM);
                postalCode = StringUtils.left(postalCode, SQL_POSTALCODE_DIM);
                country = StringUtils.left(country, SQL_COUNTRY_DIM);
                postOfficeAddress = StringUtils.left(postOfficeAddress,
                        SQL_POSTALOFFICEADDRESS_DIM);
                extendedAddress = StringUtils.left(extendedAddress,
                        SQL_EXTENDEDADDRESS_DIM);

                addressFields = new String[] {street, city, postalCode,
                country, state, postOfficeAddress, extendedAddress};

                String otherLabel = Property.stringFrom(otherAddressBook.getLabel());
                if (otherLabel != null) {
                    String[] label = {otherLabel, FIELD_OTHER_LABEL};
                    labels.add(label);
                }

                emptyAddress = hasOnlyNullContent(addressFields);

                if (!emptyAddress) {

                    if (!findRecord && !hasOnlyEmptyOrNullContent(addressFields)){

                        ps = con.prepareStatement(
                            SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                        ps.setLong(1, Long.parseLong(cw.getId()));
                        ps.setInt(2, ADDRESS_TYPE_OTHER);
                        ps.setString(3, street);
                        ps.setString(4, city);
                        ps.setString(5, state);
                        ps.setString(6, postalCode);
                        ps.setString(7, country);
                        ps.setString(8, postOfficeAddress);
                        ps.setString(9, extendedAddress);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    } else {


                        sqlUpdateFunPimAddress = new StringBuffer();

                        sqlUpdateFunPimAddress
                                .append(SQL_UPDATE_FNBL_PIM_ADDRESS_BEGIN);

                        if (street != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STREET
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (city != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_CITY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (state != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STATE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postalCode != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_POSTAL_CODE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (country != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_COUNTRY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postOfficeAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_PO_BOX
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (extendedAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_EXTENDED_ADDRESS
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }

                        sqlUpdateFunPimAddress
                                .append(SQL_FIELD_TYPE + SQL_EQUALS_QUESTIONMARK
                                + SQL_UPDATE_FNBL_PIM_ADDRESS_END);

                        ps = con
                                .prepareStatement(
                                sqlUpdateFunPimAddress.toString());

                        k = 1;

                        if (street != null) {
                            ps.setString(k++, street);
                        }
                        if (city != null) {
                            ps.setString(k++, city);
                        }
                        if (state != null) {
                            ps.setString(k++, state);
                        }
                        if (postalCode != null) {
                            ps.setString(k++, postalCode);
                        }
                        if (country != null) {
                            ps.setString(k++, country);
                        }
                        if (postOfficeAddress != null) {
                            ps.setString(k++, postOfficeAddress);
                        }
                        if (extendedAddress != null) {
                            ps.setString(k++, extendedAddress);
                        }

                        ps.setInt(k++, ADDRESS_TYPE_OTHER);
                        ps.setLong(k++, Long.parseLong(cw.getId()));
                        ps.setInt(k++, ADDRESS_TYPE_OTHER);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);

                    }

                }

            }

            //
            // work address
            //
            if (workAddressBook != null) {

                ps = con.prepareStatement(SQL_SELECT_FROM_FNBL_PIM_ADDRESS);

                ps.setLong(1, Long.parseLong(cw.getId()));
                ps.setInt(2, ADDRESS_TYPE_WORK);

                rs = ps.executeQuery();

                findRecord = rs.next();

                DBTools.close(null, ps, rs);

                street = Property.stringFrom(workAddressBook.getStreet());
                city = Property.stringFrom(workAddressBook.getCity());
                postalCode = Property.stringFrom(workAddressBook.getPostalCode());
                state = Property.stringFrom(workAddressBook.getState());
                country = Property.stringFrom(workAddressBook.getCountry());
                postOfficeAddress =
                    Property.stringFrom(workAddressBook.getPostOfficeAddress());
                extendedAddress =
                    Property.stringFrom(workAddressBook.getExtendedAddress());

                street = replaceNewLine(StringUtils.left(street, SQL_STREET_DIM));
                city = StringUtils.left(city, SQL_CITY_DIM);
                state = StringUtils.left(state, SQL_STATE_DIM);
                postalCode = StringUtils.left(postalCode, SQL_POSTALCODE_DIM);
                country = StringUtils.left(country, SQL_COUNTRY_DIM);
                postOfficeAddress = StringUtils.left(postOfficeAddress,
                        SQL_POSTALOFFICEADDRESS_DIM);
                extendedAddress = StringUtils.left(extendedAddress,
                        SQL_EXTENDEDADDRESS_DIM);

                String workLabel = Property.stringFrom(workAddressBook.getLabel());
                if (workLabel != null) {
                    String[] label = {workLabel, FIELD_BUSINESS_LABEL};
                    labels.add(label);
                }

                addressFields = new String[] {street, city, postalCode,
                country, state, postOfficeAddress, extendedAddress};

                emptyAddress = hasOnlyNullContent(addressFields);

                if (!emptyAddress) {

                    if (!findRecord && !hasOnlyEmptyOrNullContent(addressFields)){

                        ps = con.prepareStatement(
                            SQL_INSERT_INTO_FNBL_PIM_ADDRESS);

                        ps.setLong(1, Long.parseLong(cw.getId()));
                        ps.setInt(2, ADDRESS_TYPE_WORK);
                        ps.setString(3, street);
                        ps.setString(4, city);
                        ps.setString(5, state);
                        ps.setString(6, postalCode);
                        ps.setString(7, country);
                        ps.setString(8, postOfficeAddress);
                        ps.setString(9, extendedAddress);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);

                    } else {

                        sqlUpdateFunPimAddress = new StringBuffer();

                        sqlUpdateFunPimAddress
                                .append(SQL_UPDATE_FNBL_PIM_ADDRESS_BEGIN);

                        if (street != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STREET
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (city != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_CITY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (state != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_STATE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postalCode != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_POSTAL_CODE
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (country != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_COUNTRY
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (postOfficeAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_PO_BOX
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }
                        if (extendedAddress != null) {
                            sqlUpdateFunPimAddress.append(SQL_FIELD_EXTENDED_ADDRESS
                                    + SQL_EQUALS_QUESTIONMARK_COMMA);
                        }

                        sqlUpdateFunPimAddress
                                .append(SQL_FIELD_TYPE + SQL_EQUALS_QUESTIONMARK
                                + SQL_UPDATE_FNBL_PIM_ADDRESS_END);

                        ps = con.prepareStatement(
                            sqlUpdateFunPimAddress.toString());

                        k = 1;

                        if (street != null) {
                            ps.setString(k++, street);
                        }
                        if (city != null) {
                            ps.setString(k++, city);
                        }
                        if (state != null) {
                            ps.setString(k++, state);
                        }
                        if (postalCode != null) {
                            ps.setString(k++, postalCode);
                        }
                        if (country != null) {
                            ps.setString(k++, country);
                        }
                        if (postOfficeAddress != null) {
                            ps.setString(k++, postOfficeAddress);
                        }
                        if (extendedAddress != null) {
                            ps.setString(k++, extendedAddress);
                        }

                        ps.setInt(k++, ADDRESS_TYPE_WORK);
                        ps.setLong(k++, Long.parseLong(cw.getId()));
                        ps.setInt(k++, ADDRESS_TYPE_WORK);

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);

                    }

                }

            }

            //
            // labels
            //
            if (!labels.isEmpty()) {

                ps1 = con.prepareStatement(
                    SQL_CHECK_IF_IN_FNBL_PIM_CONTACT_ITEM);

                for (int i = 0, l = labels.size(); i < l; i++) {

                    String[] label = labels.get(i);

                    String labelType = label[1];

                    if ((FIELD_HOME_LABEL).equals(labelType)) {
                        type = TYPE_HOME_LABEL;
                    } else if ((FIELD_BUSINESS_LABEL).equals(labelType)) {
                        type = TYPE_BUSINESS_LABEL;
                    } else if ((FIELD_OTHER_LABEL).equals(labelType)) {
                        type = TYPE_OTHER_LABEL;
                    } else {
                        //
                        // Unknown property: saves nothing
                        //
                        continue;
                    }

                    ps1.setLong(1, Long.parseLong(cw.getId()));
                    ps1.setInt(2, type);

                    rs = ps1.executeQuery();

                    findRecord = rs.next();

                    String labelValue = StringUtils.left(label[0], SQL_LABEL_DIM);

                    if (!findRecord) {

                        if (labelValue != null
                                && labelValue.length() != 0) {
                            ps = con.prepareStatement(
                                SQL_INSERT_INTO_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                            ps.setString(3, labelValue);

                            ps.executeUpdate();

                            DBTools.close(null, ps, null);
                        }

                    } else {

                        if (labelValue != null){
                            ps = con.prepareStatement(
                                SQL_UPDATE_FNBL_PIM_CONTACT_ITEM);

                            ps.setString(1, labelValue);
                            ps.setLong(2, Long.parseLong(cw.getId()));
                            ps.setInt(3, type);
                        } else {
                            ps = con.prepareStatement(
                                SQL_DELETE_FNBL_PIM_CONTACT_ITEM);

                            ps.setLong(1, Long.parseLong(cw.getId()));
                            ps.setInt(2, type);
                        }

                        ps.executeUpdate();

                        DBTools.close(null, ps, null);
                    }

                    DBTools.close(null, null, rs);

                }

                DBTools.close(null, ps1, null);

            }
            if (photoToSet) {
                setPhoto(con, Long.parseLong(cw.getId()), photo);
            } else if (photoToRemove) {
                deletePhoto(con, Long.parseLong(cw.getId()));
            }

        } catch (Exception e) {
            throw new DAOException("Error updating contact.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return cw.getId();
    }

    /**
     * Removes a contact, provided it has the same userId as this DAO.
     * The deletion is soft (reversible).
     *
     * @param contact whence the UID and the last update Date are extracted
     * @throws DAOException
     */
    public void removeItem(ContactWrapper contact) throws DAOException {
        removeItem(contact.getId(), null);
    }

    /**
     * Gets the contact with given UID, provided it has the same userId as this
     * DAO without retrieve its photo
     *
     * @param uid corresponds to the id field in the fnbl_pim_contact table

     * @throws DAOException
     * @return the contact as a ContactWrapper object.
     */
    public ContactWrapper getItem(String uid) throws DAOException {
        return getItem(uid, false);

    }

    /**
     * Gets the contact with given UID, provided it has the same userId as this
     * DAO. The contact photo will be provide if <code>withPhoto</code> is true,
     * otherwise the contact is retrived without photo
     *
     * @param uid corresponds to the id field in the fnbl_pim_contact table
     * @param withPhoto should the contact contain its photo ?
     * @throws DAOException
     * @return the contact as a ContactWrapper object.
     */
    public ContactWrapper getItem(String uid, boolean withPhoto)
    throws DAOException {

        if (log.isTraceEnabled()) {
            log.trace("Retrieving contact '" + uid + "'");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ContactWrapper c;

        Long id = Long.parseLong(uid);

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_FNBL_PIM_CONTACT_BY_ID_USER);
            ps.setLong(1, id);
            ps.setString(2, userId);

            rs = ps.executeQuery();

            c = createContact(uid, rs);

            DBTools.close(null, ps, rs);

            ps = con.prepareStatement(SQL_GET_FNBL_PIM_CONTACT_ITEM_BY_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            try {
                addPIMContactItems(c, rs);
            } catch (SQLException sqle) {
                throw new SQLException("Error while adding extra PIM contact "
                        + "information. " + sqle.getMessage(),
                        sqle.getSQLState());
            }

            DBTools.close(null, ps, rs);

            ps = con.prepareStatement(SQL_GET_FNBL_PIM_ADDRESS_BY_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            try {
                addPIMAddresses(c, rs);
            } catch (SQLException sqle) {
                throw new SQLException("Error while adding PIM address "
                        + "information. " + sqle,
                        sqle.getSQLState());
            }

            if (withPhoto) {
                //if the photo type is null, there is nothing to do
                if (c.getPhotoType() != null) {
                    if (ContactWrapper.PHOTO_IMAGE.equals(c.getPhotoType())  ||
                        ContactWrapper.PHOTO_URL.equals(c.getPhotoType())) {
                        Photo photo = getPhoto(con, id);
                        c.getContact().getPersonalDetail().setPhotoObject(photo);
                    } else if (ContactWrapper.EMPTY_PHOTO.equals(c.getPhotoType())) {
                        c.getContact().getPersonalDetail().setPhotoObject(new Photo(null, null, null));
                    }
                }
            }


        } catch (Exception e) {
            throw new DAOException("Error retrieving contact.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return c;
    }

    /**
     * Retrieves the UID list of the contacts considered to be "twins" of a
     * given contact.
     *
     * @param c the Contact object representing the contact whose twins
     *          need to be found.
     * @return a List of UIDs (as String objects) that may be empty but not null
     * @throws DAOException if an error occurs
     */
    public List<String> getTwinItems(Contact c) throws DAOException {
        if (log.isTraceEnabled()) {
            log.trace("Retrieving twin items for the given contact...");
        }

        List<String> twins = new ArrayList<String>();
        Map<Long, Map<Integer, String>> twinsFound = 
            new HashMap<Long, Map<Integer, String>>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        if(!isTwinSearchAppliableOn(c)) {
            if (log.isTraceEnabled()) {
                    log.trace("Item with no email addresses, company name, first, "
                              + "last and display names: twin search skipped.");
            }
            return twins;
        }

        try {

            String firstName =
                c.getName().getFirstName().getPropertyValueAsString();
            String lastName =
                c.getName().getLastName().getPropertyValueAsString();
            String displayName =
                c.getName().getDisplayName().getPropertyValueAsString();
            String companyName = null;
            if (c.getBusinessDetail().getCompany() != null) {
                companyName = c.getBusinessDetail()
                               .getCompany().getPropertyValueAsString();
            }

            firstName = StringUtils.left(firstName, SQL_FIRSTNAME_DIM);
            lastName = StringUtils.left(lastName, SQL_LASTNAME_DIM);
            displayName = StringUtils.left(displayName, SQL_DISPLAYNAME_DIM);
            companyName = StringUtils.left(companyName, SQL_COMPANY_DIM);
            firstName   = normalizeField(firstName);
            lastName    = normalizeField(lastName);
            displayName = normalizeField(displayName);
            companyName = normalizeField(companyName);

            StringBuilder query = new StringBuilder(SQL_GET_POTENTIAL_TWINS);
            List<String> params = new ArrayList<String>();

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            //
            // If Funambol is not in the debug mode is not possible to print the
            // contact because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(100);
                    sb.append("Looking for items having: ")
                    .append("\n> first name   : '").append(toPrintableString(firstName)).append('\'')
                    .append("\n> last  name   : '").append(toPrintableString(lastName)).append('\'')
                    .append("\n> display name : '").append(toPrintableString(displayName)).append('\'')
                    .append("\n> company name : '").append(toPrintableString(companyName)).append('\'');

                    log.trace(sb.toString());
                }
            }

            boolean isUnnamedContact =
                    StringUtils.isEmpty(firstName)
                    && StringUtils.isEmpty(lastName)
                    && StringUtils.isEmpty(displayName)
                    && StringUtils.isEmpty(companyName);

            if (isUnnamedContact) {

                if (unnamedContacts == null) {

                    query.append(SQL_UNNAMED_WHERE_CLAUSES);
                    query.append(SQL_STATUS_NOT_D);
                    query.append(SQL_ORDER_BY_ID);

                    params.add(userId);
                    params.add(firstName);
                    params.add(lastName);
                    params.add(companyName);
                    params.add(displayName);

                    ps = con.prepareStatement(query.toString());

                    int cont = 1;
                    for (String param : params) {
                        ps.setString(cont++, param);
                    }

                    rs = ps.executeQuery();

                    //slipts query result in a better organized data structure
                    //-contact id
                    //  -item type, item value
                    //  -item type, item value
                    //  -...
                    //-contact id
                    //  -...
                    unnamedContacts = getTwinsItemsFromRecordset(rs);

                    if (log.isTraceEnabled()) {
                        log.trace("Found '"+unnamedContacts.size()+
                            "' potential twin unnamed contacts with ids '"+
                            unnamedContacts.keySet().toString()+"'");
                    }
                    DBTools.close(null, null, rs);
                }

                // returns only the twin items
                twinsFound =
                    retrievePotentialTwinsComparingEmailsAndPhoneNumbers(c, unnamedContacts, isUnnamedContact);
                
            } else {

                params.add(userId);

                query.append(" AND (");
                if ("".equals(firstName)) {
                    query.append(" (c.first_name is null) OR ");
                }
                query.append(" (lower(c.first_name) = ?) ");
                params.add(firstName.toLowerCase());
                query.append(" )");

                query.append(" AND (");
                if ("".equals(lastName)) {
                    query.append(" (c.last_name is null) OR ");
                }
                query.append(" (lower(c.last_name) = ?) ");
                params.add(lastName.toLowerCase());
                query.append(" )");
                //
                // Only if the first name and last name are empty,
                // the company is used in the research of twin items.
                //                
                if ("".equals(firstName) && "".equals(lastName)) {

                     query.append(" AND (");

                     if ("".equals(companyName)) {
                         query.append(" (c.company is null) OR ");
                     }
                     query.append(" (lower(c.company) = ?) ");
                     params.add(companyName.toLowerCase());
                     query.append(" )");

                     //
                     // Only if the first name, last name and company are empty,
                     // the display name is used in the research of twin items.
                     //
                     if ("".equals(companyName)) {

                         query.append(" AND (");
                         if ("".equals(displayName)) {
                             query.append(" (c.display_name is null) OR ");
                         }
                         query.append(" (lower(c.display_name) = ?) ");
                         params.add(displayName.toLowerCase());
                         query.append(" ) ");
                     }
                }

                query.append(SQL_STATUS_NOT_D);
                query.append(SQL_ORDER_BY_ID);

                ps = con.prepareStatement(query.toString());

                int cont = 1;
                for (String param : params) {
                    ps.setString(cont++, param);
                }
                
                rs = ps.executeQuery();

                //slipts query result in a better organized data structure
                //-contact id
                //  -item type, item value
                //  -item type, item value
                //  -...
                //-contact id
                //  -...
                Map<Long, Map<Integer, String>> twinsInfo = 
                    getTwinsItemsFromRecordset(rs);

                if (log.isTraceEnabled()) {
                        log.trace("Found '"+twinsInfo.size()+
                            "' potential twin contacts with ids '"+
                            twinsInfo.keySet().toString()+"'");
                }
                DBTools.close(null, null, rs);

                //returns only the twin items
                twinsFound=
                    retrievePotentialTwinsComparingEmailsAndPhoneNumbers(c, twinsInfo, isUnnamedContact);
            }

            for (Long twinId : twinsFound.keySet()) {
                if (log.isTraceEnabled()) {
                    log.trace("Found twin '" + twinId + "'");
                }
                twins.add(Long.toString(twinId));
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving contact twin items", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return twins;
    }

    /**
     * Sets the contact identified with the given id with the given photo.
     * Moreover, the contact will be marked as updated.
     * <br/>
     * If the contact has already a photo, that will be updated with the new one.
     * <br/>
     * If the given photo is null, the one in the db (if there is) will be removed.
     *
     * @param id contact id
     * @param photo the photo to set
     * @throws com.funambol.foundation.exception.DAOException if an error occurs
     */
    public void setContactPhoto(Long id, Photo photo)
    throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            setContactPhoto(conn, id, photo);
        } catch (Exception ex) {
            throw new DAOException("Error setting photo", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Sets the contact identified with the given id with the given photo using
     * the given connection.
     * Moroever, the contact will be marked as updated.
     * <br/>
     * If the contact has already a photo, that will be updated with the new one.
     * <br/>
     * If the given photo is null, the one in the db (if there is) will be removed.
     *
     * @param conn the connection to use
     * @param id contact id
     * @param photo the photo to set
     * @throws com.funambol.foundation.exception.DAOException if an error occurs
     */
    public void setContactPhoto(Connection conn, Long id, Photo photo)
    throws DAOException {

        if (photo != null) {
            if (photo.getUrl() == null && photo.getImage() == null) {
                deleteContactPhoto(conn, id);
                setPhotoType(conn, id, ContactWrapper.EMPTY_PHOTO  );
            }
        } else {
            deleteContactPhoto(conn, id);
            setPhotoType(conn, id, ContactWrapper.EMPTY_PHOTO  );
        }

        if (!updatePhoto(conn, id, photo)) {
            insertPhoto(conn, id, photo);
        }
        setPhotoType(conn, id, photo);
    }

    /**
     * Returns the photo of the contact identified by the given id or null if
     * not found.
     * @param id the contact id
     * @return the photo
     * @throws com.funambol.foundation.exception.DAOException if an error occurs
     */
    public Photo getPhoto(Long id) throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            conn.setReadOnly(true);

            return getPhoto(conn, id);
        } catch (Exception ex) {
            throw new DAOException("Error retrieving photo with id: " + id, ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Returns the photo with the given id using the given connection.
     * <p>Note that the connection is not closed at the end of the method
     * @param con the connection to use
     * @param id the if
     * @return the photo, or null if not found
     * @throws DAOException if an error occurs
     */
    public Photo getPhoto(Connection con, Long id) throws DAOException {

        Photo photo  = null;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            stmt = con.prepareStatement(SQL_SELECT_FROM_FNBL_PIM_CONTACT_PHOTO);
            stmt.setLong(1, id);
            stmt.setString(2, userId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                photo = new Photo();
                photo.setType(rs.getString(2));
                photo.setImage(rs.getBytes(3));
                photo.setUrl(rs.getString(4));
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving photo with id: " + id, e);
        } finally {
            DBTools.close(null, stmt, rs);
        }
        return photo;
    }

    /**
     * Deletes the photo for the contact with the given id
     * @param contactId the contac id
     * @return true if the photo has been removed
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    public boolean deleteContactPhoto(Long contactId)
    throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            return deleteContactPhoto(conn, contactId);
        } catch (Exception ex) {
            throw new DAOException("Error removing photo for contact: '" +
                                           contactId + "'", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Deletes the photo for the contact with the given id using the given connection
     * @param con the connection to use
     * @param contactId the contac id
     * @return true if the photo has been removed
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    public boolean deleteContactPhoto(Connection con, Long contactId)
    throws DAOException {
        boolean photoDeleted = deletePhoto(con, contactId);
        setPhotoType(con, contactId, (Short)null);
        return photoDeleted;
    }

    /**
     * This method allows to understand if is possible to run the twin search
     * on the given contact.
     * Fields used in the twin search are:
     * -  firstName
     * -  lastName
     * -  displayName
     * -  companyName
     * -  at least one email address
     * -  at least one phone number
     *
     * @param contact the contact we want to check
     *
     * @return true if at least one field used for twin search contains 
     * meaningful data, false otherwise
     */
    public boolean isTwinSearchAppliableOn(Contact contact) {

        if(contact==null) return false;

        boolean hasAtLeastOneValidEmail = hasAtLeastOneNonEmptyProperty(
                contact.getPersonalDetail().getEmails(),
                contact.getBusinessDetail().getEmails());

        boolean hasAtLeastOneValidPhone = hasAtLeastOneNonEmptyProperty(
                contact.getPersonalDetail().getPhones(),
                contact.getBusinessDetail().getPhones());

        String firstName =
            contact.getName().getFirstName().getPropertyValueAsString();
        String lastName =
            contact.getName().getLastName().getPropertyValueAsString();
        String displayName =
            contact.getName().getDisplayName().getPropertyValueAsString();
        String companyName = null;
        if (contact.getBusinessDetail().getCompany() != null) {
            companyName = contact.getBusinessDetail()
                                 .getCompany().getPropertyValueAsString();
        }

        firstName   = normalizeField(firstName);
        lastName    = normalizeField(lastName);
        displayName = normalizeField(displayName);
        companyName = normalizeField(companyName);

        return (firstName.length()   > 0 ||
                lastName.length()    > 0 ||
                companyName.length() > 0 ||
                displayName.length() > 0 ||
                hasAtLeastOneValidEmail  ||
                hasAtLeastOneValidPhone    );
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Sets the given photo for the contact identified by the given id.
     * BTW, the contact is not updated. To update the contact use setContactPhoto(...)
     * @param id the contact id
     * @param photo the photo to set
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void setPhoto(Long id, Photo photo)
    throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            setPhoto(conn, id, photo);
        } catch (Exception ex) {
            throw new DAOException("Error setting photo", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Sets the given photo for the contact identified by the given id.
     * BTW, the contact is not updated. To update the contact use setContactPhoto(...)
     * @param conn the connection to use
     * @param id the contact id
     * @param photo the photo to set
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void setPhoto(Connection conn, Long id, Photo photo)
    throws DAOException {

        if (!updatePhoto(conn, id, photo)) {
            insertPhoto(conn, id, photo);
        }
    }

    /**
     * Adds the given photo for the contact identified by the given id. Also the
     * contact is updated accordlying.
     * @param contactId the contact id
     * @param photo the photo to add
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void addContactPhoto(Long contactId, Photo photo)
    throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            insertPhoto(conn, contactId, photo);
            setPhotoType(conn, contactId, photo);
        } catch (Exception ex) {
            throw new DAOException("Error storing photo", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Adds the given photo for the contact identified by the given id using the
     * given connection. Also the contact is updated accordlying.
     * @param con the connection to use
     * @param contactId the contact id
     * @param photo the photo to add
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void addContactPhoto(Connection con, Long contactId, Photo photo)
    throws DAOException {

        insertPhoto(con, contactId, photo);

        setPhotoType(con, contactId, photo);
    }

    /**
     * Updates the given photo for the contact identified by the given id.
     * Also the contact is updated accordlying.
     * @param contactId the contact id
     * @param photo the photo to add
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void updateContactPhoto(Long contactId, Photo photo)
    throws DAOException {
        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            updateContactPhoto(conn, contactId, photo);
        } catch (Exception ex) {
            throw new DAOException("Error updating photo", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Updates the given photo for the contact identified by the given id using
     * the given connection.
     * Also the contact is updated accordlying.
     * @param con the connection to use
     * @param contactId the contact id
     * @param photo the photo to add
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void updateContactPhoto(Connection con, Long contactId, Photo photo)
    throws DAOException {

        updatePhoto(con, contactId, photo);

        setPhotoType(con, contactId, photo);

    }

    /**
     * Deletes the photo identified by the given id. The contact is NOT update.
     * Use deleteContactPhoto to update also the contact.
     * @param id the photo id
     * @return true if the photo is deleted, false
     * @throws com.funambol.foundation.exception.DAOException
     */
    protected boolean deletePhoto(Long id)
    throws DAOException {

        Connection conn = null;
        try {
            conn = getUserDataSource().getRoutedConnection(userId);
            return deletePhoto(conn, id);
        } catch (Exception ex) {
            throw new DAOException("Error deleting photo with id: '" + id + "'", ex);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    /**
     * Deletes the photo identified by the given id. The contact is NOT update.
     * Use deleteContactPhoto to update also the contact.
     * @param con the connection to use
     * @param id the photo id
     * @return true if the photo is deleted, false
     * @throws com.funambol.foundation.exception.DAOException
     */
    protected boolean deletePhoto(Connection con, Long id)
    throws DAOException {

        if (!verifyPermission(con, id)) {
            throw new DAOException("Contact '" + id +
                                           " is not a contact of the user '" +
                                           userId + "'");
        }

        PreparedStatement stmt = null;

        int numDeletedRows  = 0;
        try {
            stmt = con.prepareStatement(SQL_DELETE_FNBL_PIM_CONTACT_PHOTO);

            stmt.setLong(1, id);

            numDeletedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DAOException("Error deleting photo with id: '" + id + "'", ex);
        } finally {
            DBTools.close(null, stmt, null);
        }
        return (numDeletedRows == 1);
    }

    /**
     * Updates the property 'photoType' of the contact with the given id to the
     * given value according to the given photo. If the photo is null, ContactWrapper.NO_PHOTO
     * is set; if the photo is not null and the photo contains an image, ContactWrapper.PHOTO_TYPE_IMAGE
     * is set; if the photo is not null and the photo contains an url, ContactWrapper.PHOTO_TYPE_URL
     * is set; otherwise ContactWrapper.NO_PHOTO is set
     * <br/>
     * The contact is moreover marked as UPDATED and its last_update is updated
     * @param con the connection to use
     * @param contactId the contact to update
     * @param photo the photo
     * @return the number of the updated rows
     * @throws DAOException if an error occurs
     */
    protected int setPhotoType(Connection con, Long contactId, Photo photo)
    throws DAOException {
        if (photo != null) {
            if (photo.getImage() != null) {
                return setPhotoType(con, contactId, ContactWrapper.PHOTO_IMAGE);
            } else if (photo.getUrl() != null) {
                return setPhotoType(con, contactId, ContactWrapper.PHOTO_URL);
            } else {
                return setPhotoType(con, contactId, ContactWrapper.EMPTY_PHOTO);
            }
        }
        return setPhotoType(con, contactId, (Short)null);
    }

    /**
     * Updates the given photo for the contact identified by the given id using
     * the given connection.
     * Note the contact is NOT updated accordlying. Use updateContactPhoto to
     * update also the contact.
     * @param con the connection to use
     * @param id the contact id
     * @param photo the photo to update
     * @return true if the photo has been updated, false
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected boolean updatePhoto(Connection con, Long id, Photo photo)
    throws DAOException {

        if (!verifyPermission(con, id)) {
            throw new DAOException("Contact '" + id +
                                           " is not a contact of the user '" +
                                           userId + "'");
        }

        PreparedStatement stmt = null;

        byte[] image = null;
        String type  = null;
        String url   = null;

        if (photo != null) {
            image = photo.getImage();
            type  = photo.getType();
            url   = photo.getUrl();
        }

        int numUpdatedRows  = 0;
        try {

            stmt = con.prepareStatement(SQL_UPDATE_FNBL_PIM_CONTACT_PHOTO);

            if (type == null) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, type);
            }

            if (url == null) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, url);
            }

            if (image == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBinaryStream(3, new ByteArrayInputStream(image), image.length);
            }

            stmt.setLong(4, id);

            numUpdatedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DAOException("Error updating photo", ex);
        } finally {
            DBTools.close(null, stmt, null);
        }
        return (numUpdatedRows == 1);
    }

    /**
     * Changes the contact status with the given parameters
     * @param con the connection to use
     * @param contactId the contact id
     * @param status the status to set
     * @return true if the contact has been updated, false otherwise
     * @throws com.funambol.foundation.exception.DAOException
     */
    protected boolean changeContactStatus(Connection con, Long contactId, char status)
    throws DAOException {
        PreparedStatement stmt = null;

        int numUpdatedRows  = 0;
        try {
            stmt = con.prepareStatement(SQL_UPDATE_FNBL_PIM_CONTACT_STATUS);

            stmt.setString(1, String.valueOf(status));
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setLong(3, contactId);
            stmt.setString(4, userId);

            numUpdatedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DAOException("Error changing status of contact '" +
                                           contactId + "' to '" + status + "'", ex);
        } finally {
            DBTools.close(null, stmt, null);
        }
        return (numUpdatedRows == 1);
    }

    /**
     * Inserts the photo identified by the given id. The contact is NOT update.
     * Use deleteContactPhoto to update also the contact.
     * @param con the connection to use
     * @param contactId the photo id
     * @param photo the Photo to insert
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    protected void insertPhoto(Connection con, Long contactId, Photo photo)
    throws DAOException {

        if (!verifyPermission(con, contactId)) {
            throw new DAOException("Contact '" + contactId +
                                   " is not a contact of the user '" +
                                   userId + "'");
        }

        PreparedStatement stmt = null;

        if (photo == null) {
            return ;
        }
        byte[] image = photo.getImage();

        try {
            stmt = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CONTACT_PHOTO);

            stmt.setLong(1, contactId);

            if (photo.getType() == null) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, photo.getType());
            }

            if (image == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBinaryStream(3, new ByteArrayInputStream(image), image.length);
            }

            if (photo.getUrl() == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, photo.getUrl());
            }

            stmt.execute();

        } catch (SQLException ex) {
            throw new DAOException("Error storing photo", ex);
        } finally {
            DBTools.close(null, stmt, null);
        }
    }

    //---------------------------------------------------------- Private methods

    /**
     * Creates a ContactWrapper object from a ResultSet. Only the basic data are
     * set.
     *
     * @param wrapperId the UID of the wrapper object to be returned
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the fnbl_pim_contact table, with the cursor before its first row
     * @return a newly created ContactWrapper initialized with the fields in the
     *         result set
     * @throws java.sql.SQLException
     * @throws NotFoundException
     */
    private static ContactWrapper createContact(String wrapperId, ResultSet rs)
    throws SQLException, NotFoundException {

        if (!rs.next()) {
            throw new NotFoundException("No contact found.");
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        ContactWrapper cw = null;
        Note note = null;
        Title title = null;

        String column = null;

        String userId = rs.getString(SQL_FIELD_USERID);

        Contact c = new Contact();
        cw = new ContactWrapper(wrapperId, userId, c);

        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; ++i) {

            column = rsmd.getColumnName(i);

            //
            // General
            //
            if (SQL_FIELD_ID.equalsIgnoreCase(column)) {
                // Does nothing: field already set at construction time
            } else if (SQL_FIELD_LAST_UPDATE.equalsIgnoreCase(column)) {
                cw.setLastUpdate(new Timestamp(rs.getLong(i)));
            } else if (SQL_FIELD_USERID.equalsIgnoreCase(column)) {
                // Does nothing: field already set at construction time
            } else if (SQL_FIELD_STATUS.equalsIgnoreCase(column)) {
                cw.setStatus(rs.getString(i).charAt(0));
            } else if (SQL_FIELD_PHOTO_TYPE.equalsIgnoreCase(column)) {
                short phType = rs.getShort(i);
                if (!rs.wasNull()) {
                    cw.setPhotoType(Short.valueOf(phType));
                }
                //
                // contact details
                //
            } else if (SQL_FIELD_IMPORTANCE.equalsIgnoreCase(column)) {
                short importance = rs.getShort(i);
                if (!rs.wasNull()) {
                    c.setImportance(Short.valueOf(importance));
                }
            } else if (SQL_FIELD_SENSITIVITY.equalsIgnoreCase(column)) {
                short sensitivity = rs.getShort(i);
                if (!rs.wasNull()) {
                    c.setSensitivity(Short.valueOf(sensitivity));
                }
            } else if (SQL_FIELD_SUBJECT.equalsIgnoreCase(column)) {
                c.setSubject(rs.getString(i));
            } else if (SQL_FIELD_FOLDER.equalsIgnoreCase(column)) {
                c.setFolder(rs.getString(i));
                //
                // Personal details
                //
            } else if (SQL_FIELD_ANNIVERSARY.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setAnniversary(rs.getString(i));
            } else if (SQL_FIELD_FIRST_NAME.equalsIgnoreCase(column)) {
                c.getName().getFirstName().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_MIDDLE_NAME.equalsIgnoreCase(column)) {
                c.getName().getMiddleName().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_LAST_NAME.equalsIgnoreCase(column)) {
                c.getName().getLastName().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_DISPLAY_NAME.equalsIgnoreCase(column)) {
                c.getName().getDisplayName().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_BIRTHDAY.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setBirthday(rs.getString(i));
            } else if (SQL_FIELD_BODY.equalsIgnoreCase(column)) {
                String noteStr = rs.getString(i);
                if (noteStr != null) {
                    note = new Note();
                    note.setNoteType(FIELD_NOTE);
                    note.setPropertyValue(noteStr);
                    c.addNote(note);
                }
            } else if (SQL_FIELD_CATEGORIES.equalsIgnoreCase(column)) {
                c.getCategories().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_CHILDREN.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setChildren(rs.getString(i));
            } else if (SQL_FIELD_HOBBIES.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setHobbies(rs.getString(i));
            } else if (SQL_FIELD_GENDER.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setGender(rs.getString(i));
            } else if (SQL_FIELD_INITIALS.equalsIgnoreCase(column)) {
                c.getName().getInitials().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_LANGUAGES.equalsIgnoreCase(column)) {
                c.setLanguages(rs.getString(i));
            } else if (SQL_FIELD_NICKNAME.equalsIgnoreCase(column)) {
                c.getName().getNickname().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_SPOUSE.equalsIgnoreCase(column)) {
                c.getPersonalDetail().setSpouse(rs.getString(i));
            } else if (SQL_FIELD_SUFFIX.equalsIgnoreCase(column)) {
                c.getName().getSuffix().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_TITLE.equalsIgnoreCase(column)) {
                c.getName().getSalutation().setPropertyValue(rs.getString(i));
                //
                // Business details
                //
            } else if (SQL_FIELD_ASSISTANT.equalsIgnoreCase(column)) {
                c.getBusinessDetail().setAssistant(rs.getString(i));
            } else if (SQL_FIELD_COMPANY.equalsIgnoreCase(column)) {
                c.getBusinessDetail().getCompany().setPropertyValue(
                        rs.getString(i));
            } else if (SQL_FIELD_COMPANIES.equalsIgnoreCase(column)) {
                c.getBusinessDetail().setCompanies(rs.getString(i));
            } else if (SQL_FIELD_DEPARTMENT.equalsIgnoreCase(column)) {
                c.getBusinessDetail().getDepartment().setPropertyValue(
                        rs.getString(i));
            } else if (SQL_FIELD_JOB_TITLE.equalsIgnoreCase(column)) {
                String titleStr = null;
                titleStr = rs.getString(i);
                if (titleStr != null) {
                    title = new Title();
                    title.setTitleType(FIELD_JOB_TITLE);
                    title.setPropertyValue(titleStr);
                    c.getBusinessDetail().addTitle(title);
                }
            } else if (SQL_FIELD_MANAGER.equalsIgnoreCase(column)) {
                c.getBusinessDetail().setManager(rs.getString(i));
            } else if (SQL_FIELD_MILEAGE.equalsIgnoreCase(column)) {
                c.setMileage(rs.getString(i));
            } else if (SQL_FIELD_OFFICE_LOCATION.equalsIgnoreCase(column)) {
                c.getBusinessDetail().setOfficeLocation(rs.getString(i));
            } else if (SQL_FIELD_PROFESSION.equalsIgnoreCase(column)) {
                c.getBusinessDetail().getRole().setPropertyValue(
                        rs.getString(i));
            } else {
                throw new SQLException("Unhandled column: " + column);
            }
        }

        return cw;
    }

    /**
     * Attaches the address(es) to a contact on the basis of a ResultSet.
     *
     * @param cw the contact (as a ContactWrapper) still lacking address
     *           information
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the fnbl_pim_address table, with the cursor before its first
     *           row
     * @return the ContactWrapper object with address information attached
     * @throws java.sql.SQLException
     */
    private static ContactWrapper addPIMAddresses(ContactWrapper cw,
            ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        Contact c = cw.getContact();

        int type = 0;
        String street = null;
        String city = null;
        String state = null;
        String postalCode = null;
        String country = null;
        String poBox = null;
        String extendedAddress = null;

        String column = null;

        int columnCount = 0;

        while (rs.next()) {

            columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; ++i) {

                column = rsmd.getColumnName(i);

                if (SQL_FIELD_TYPE.equalsIgnoreCase(column)) {
                    type = rs.getInt(i);
                } else if (SQL_FIELD_STREET.equalsIgnoreCase(column)) {
                    street = rs.getString(i);
                } else if (SQL_FIELD_CITY.equalsIgnoreCase(column)) {
                    city = rs.getString(i);
                } else if (SQL_FIELD_STATE.equalsIgnoreCase(column)) {
                    state = rs.getString(i);
                } else if (SQL_FIELD_POSTAL_CODE.equalsIgnoreCase(column)) {
                    postalCode = rs.getString(i);
                } else if (SQL_FIELD_COUNTRY.equalsIgnoreCase(column)) {
                    country = rs.getString(i);
                } else if (SQL_FIELD_PO_BOX.equalsIgnoreCase(column)) {
                    poBox = rs.getString(i);
                } else if (SQL_FIELD_EXTENDED_ADDRESS.equalsIgnoreCase(column)) {
                    extendedAddress = rs.getString(i);
                } else {
                    throw new SQLException("Unhandled column: " + column);
                }
            }

            switch (type) {

                case ADDRESS_TYPE_HOME:
                    c.getPersonalDetail().getAddress().getStreet()
                    .setPropertyValue(street);
                    c.getPersonalDetail().getAddress().getCity()
                    .setPropertyValue(city);
                    c.getPersonalDetail().getAddress().getState()
                    .setPropertyValue(state);
                    c.getPersonalDetail().getAddress().getPostalCode()
                    .setPropertyValue(postalCode);
                    c.getPersonalDetail().getAddress().getCountry()
                    .setPropertyValue(country);
                    c.getPersonalDetail().getAddress().getPostOfficeAddress()
                    .setPropertyValue(poBox);
                    c.getPersonalDetail().getAddress().getExtendedAddress()
                    .setPropertyValue(extendedAddress);
                    break;
                case ADDRESS_TYPE_OTHER:
                    c.getPersonalDetail().getOtherAddress().getStreet()
                    .setPropertyValue(street);
                    c.getPersonalDetail().getOtherAddress().getCity()
                    .setPropertyValue(city);
                    c.getPersonalDetail().getOtherAddress().getState()
                    .setPropertyValue(state);
                    c.getPersonalDetail().getOtherAddress().getPostalCode()
                    .setPropertyValue(postalCode);
                    c.getPersonalDetail().getOtherAddress().getCountry()
                    .setPropertyValue(country);
                    c.getPersonalDetail().getOtherAddress()
                    .getPostOfficeAddress().setPropertyValue(poBox);
                    c.getPersonalDetail().getOtherAddress().getExtendedAddress()
                    .setPropertyValue(extendedAddress);
                    break;
                case ADDRESS_TYPE_WORK:
                    c.getBusinessDetail().getAddress().getStreet()
                    .setPropertyValue(street);
                    c.getBusinessDetail().getAddress().getCity()
                    .setPropertyValue(city);
                    c.getBusinessDetail().getAddress().getState()
                    .setPropertyValue(state);
                    c.getBusinessDetail().getAddress().getPostalCode()
                    .setPropertyValue(postalCode);
                    c.getBusinessDetail().getAddress().getCountry()
                    .setPropertyValue(country);
                    c.getBusinessDetail().getAddress().getPostOfficeAddress()
                    .setPropertyValue(poBox);
                    c.getBusinessDetail().getAddress().getExtendedAddress()
                    .setPropertyValue(extendedAddress);
                    break;
                default:
                    break;
            }
        }

        return cw;
    }

    /**
     * Attaches extra information to a contact on the basis of a ResultSet.
     *
     * @param cw the contact still lacking extra information
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the fnbl_pim_contact_item table, with the cursor before its
     *           first row
     * @return the ContactWrapper object with extra information attached
     * @throws java.sql.SQLException
     */
    private static ContactWrapper addPIMContactItems(ContactWrapper cw,
            ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        Contact c = cw.getContact();

        int type = 0;
        String value = null;
        int columnCount = 0;
        String column = null;
        Phone phone;
        Email email;
        WebPage webPage;

        while (rs.next()) {

            columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; ++i) {

                column = rsmd.getColumnName(i);

                if (SQL_FIELD_TYPE.equalsIgnoreCase(column)) {
                    type = rs.getInt(i);
                } else if (SQL_FIELD_VALUE.equalsIgnoreCase(column)) {
                    value = rs.getString(i);
                } else {
                    throw new SQLException("Unhandled column: " + column);
                }
            }

            switch (type) {

                case TYPE_HOME_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_HOME_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_HOME_FAX_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_HOME_FAX_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_MOBILE_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_MOBILE_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_EMAIL_1_ADDRESS:
                    email = new Email();
                    email.setEmailType(FIELD_EMAIL_1_ADDRESS);
                    email.setPropertyValue(value);
                    c.getPersonalDetail().addEmail(email);
                    break;
                case TYPE_WEB_PAGE:
                    webPage = new WebPage();
                    webPage.setWebPageType(FIELD_WEB_PAGE);
                    webPage.setPropertyValue(value);
                    c.getPersonalDetail().addWebPage(webPage);
                    break;
                case TYPE_BUSINESS_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_BUSINESS_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_BUSINESS_FAX_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_BUSINESS_FAX_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_COMPANY_MAIN_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_COMPANY_MAIN_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_ASSISTANT_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_ASSISTANT_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_PAGER_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_PAGER_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_CALLBACK_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_CALLBACK_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_EMAIL_2_ADDRESS:
                    email = new Email();
                    email.setEmailType(FIELD_EMAIL_2_ADDRESS);
                    email.setPropertyValue(value);
                    c.getPersonalDetail().addEmail(email);
                    break;
                case TYPE_EMAIL_3_ADDRESS:
                    email = new Email();
                    email.setEmailType(FIELD_EMAIL_3_ADDRESS);
                    email.setPropertyValue(value);
                    c.getBusinessDetail().addEmail(email);
                    break;
                case TYPE_HOME_WEB_PAGE:
                    webPage = new WebPage();
                    webPage.setWebPageType(FIELD_HOME_WEB_PAGE);
                    webPage.setPropertyValue(value);
                    c.getPersonalDetail().addWebPage(webPage);
                    break;
                case TYPE_CAR_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_CAR_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_PRIMARY_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_PRIMARY_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_HOME_2_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_HOME_2_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_BUSINESS_2_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_BUSINESS_2_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_BUSINESS_WEB_PAGE:
                    webPage = new WebPage();
                    webPage.setWebPageType(FIELD_BUSINESS_WEB_PAGE);
                    webPage.setPropertyValue(value);
                    c.getBusinessDetail().addWebPage(webPage);
                    break;
                case TYPE_INSTANT_MESSENGER:
                    email = new Email();
                    email.setEmailType(FIELD_INSTANT_MESSENGER);
                    email.setPropertyValue(value);
                    c.getPersonalDetail().addEmail(email);
                    break;
                case TYPE_BUSINESS_LABEL:
                    c.getBusinessDetail().getAddress().getLabel()
                            .setPropertyValue(value);
                    break;
                case TYPE_OTHER_LABEL:
                    c.getPersonalDetail().getOtherAddress().getLabel()
                            .setPropertyValue(value);
                    break;
                case TYPE_HOME_LABEL:
                    c.getPersonalDetail().getAddress().getLabel()
                            .setPropertyValue(value);
                    break;
                case TYPE_OTHER_FAX_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_OTHER_FAX_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_OTHER_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_OTHER_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;
                case TYPE_TELEX_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_TELEX_NUMBER);
                    phone.setPropertyValue(value);
                    c.getBusinessDetail().addPhone(phone);
                    break;
                case TYPE_RADIO_TELEPHONE_NUMBER:
                    phone = new Phone();
                    phone.setPhoneType(FIELD_RADIO_TELEPHONE_NUMBER);
                    phone.setPropertyValue(value);
                    c.getPersonalDetail().addPhone(phone);
                    break;

                default:
                    break;
            }
        }

        return cw;
    }

    /**
     * Checks whether an array of String objects has some non-white content.
     *
     * @param strings could also be null
     * @return false only if the array contains at least a non-null string
     *               having some content different from white spaces
     */
    private static boolean hasOnlyEmptyOrNullContent(String[] strings) {

        if (strings == null) {
            return true;
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null && strings[i].length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether an array of String objects has some non-null content.
     *
     * @param strings could also be null
     * @return false only if the array contains at least a string not being null
     */
    private static boolean hasOnlyNullContent(String[] strings) {

        if (strings == null) {
            return true;
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Strips away new line characters from a string.
     *
     * @param string a String object, possibly made of several lines of text
     * @return the modified string, having all new line characters replaced by
     *         white spaces.
     */
    private static String replaceNewLine(String string) {

        if (string != null) {
            char[] nl = { 10 };
            String newLine = new String(nl);
            string = string.replaceAll(newLine, " ");
        }
        return string;
    }

    /**
     * Updates the property 'photoType' of the contact with the given id to the
     * given value using the given connection.
     * <br/>
     * The contact is moreover marked as UPDATED and its last_update is updated
     * @param con the connection to use
     * @param contactId the contact to update
     * @param photoType the type of the photo
     * @return the number of the updated rows
     * @throws DAOException if an error occurs
     */
    private int setPhotoType(Connection con, Long contactId, Short photoType)
    throws DAOException {

        if (!ContactWrapper.EMPTY_PHOTO.equals(photoType) &&
            !ContactWrapper.PHOTO_IMAGE.equals(photoType) &&
            !ContactWrapper.PHOTO_URL.equals(photoType)   &&
            photoType != null) {
            throw new IllegalArgumentException(photoType + " is not a valid photoType");
        }

        PreparedStatement stmt = null;

        int numUpdatedRows  = 0;
        try {
            stmt = con.prepareStatement(SQL_UPDATE_FNBL_PIM_CONTACT_PHOTO_TYPE);

            stmt.setString(1, String.valueOf(Def.PIM_STATE_UPDATED));
            stmt.setLong  (2, System.currentTimeMillis());
            if (photoType != null) {
                stmt.setShort(3, photoType);
            } else {
                stmt.setNull(3, Types.SMALLINT);
            }
            stmt.setLong  (4, contactId);
            stmt.setString(5, userId);

            numUpdatedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DAOException("Error updating '" + contactId + "'", ex);
        } finally {
            DBTools.close(null, stmt, null);
        }
        return numUpdatedRows;
    }

    /**
     * Verify if the contact identified by the given id is associated to the user
     * used to create the dao
     * @param con the connection to use
     * @param contactId the contact id
     * @return true if the contact is associated to the user used to create the
     *         dao, false otherwise
     * @throws com.funambol.foundation.exception.DAOException if an error
     *         occurs
     */
    private boolean verifyPermission(Connection con, Long contactId)
    throws DAOException {

        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        boolean contactFound = false;
        try {

            stmt = con.prepareStatement(SQL_GET_CONTACT_ID_BY_ID_AND_USER_ID);
            stmt.setLong(1, contactId);
            stmt.setString(2, userId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                contactFound = true;
            }

        } catch (Exception e) {
            throw new DAOException("Error checking contact '" + contactId + "'", e);
        } finally {
            DBTools.close(null, stmt, rs);
        }
        return contactFound;
    }

    /**
     *
     *
     * @param fieldValue the value of the field to normalize
     *
     * @return the normalized field, the field itself if it's not null
     */
    private String normalizeField(String fieldValue) {
       if (fieldValue == null || ("null".equals(fieldValue))) {
           return "";
       }
       return fieldValue;
    }

    /**
     *
     * @param fieldValue the string we want to log
     *
     * @return a log printable representation of the given string
     */
    private String toPrintableString(String fieldValue) {
        return (fieldValue!=null && fieldValue.length()>0)?
                fieldValue:
                UNSET_FIELD_PLACEHOLDER;
    }
    
    /**
     * Return the query to use to retrieve all the Items belonging to a user
     * @return
     */
    @Override
    protected String getAllItemsQuery() {

        return SQL_GET_FNBL_PIM_CONTACT_ID_LIST_BY_USER + SQL_ORDER_BY_ID;
    }

    /**
     * Return the query string to use to remove the Item belonging to a user
     * @return the query string to use to remove the Item belonging to a user
     */
    @Override
    protected String getRemoveItemQuery() {
        return SQL_DELETE_CONTACT_BY_ID_USERID;
    }

    /**
     * Return the query string to use to remove the all Items belonging to a user
     * @return the query string to use to remove the all Items belonging to a user
     */
    @Override
    protected String getRemoveAllItemsQuery() {
        return SQL_DELETE_CONTACTS_BY_USERID;
    }

    /**
     * Return the query string to use to retrieve the status of an Items 
     * belonging to a user
     * @return the query string to use to retrieve the status of an Items 
     * belonging to a user
     */
    @Override
    protected String getItemStateQuery() {
        return SQL_GET_STATUS_BY_ID_USER_TIME;
    }

    @Override
    protected String getChangedItemsQuery() {
        return SQL_GET_CHANGED_CONTACTS_BY_USER_AND_LAST_UPDATE;
    }

    /**
     * Read data from twin query and put it inside a Map.
     * First map has the contact id as Key and a map of contact items as their values.
     * Second map has the item type as Key and the item value as Value.
     *
     * @param rs the ResultSet to check
     * @return the map of potential twins
     */
    private Map<Long, Map<Integer, String>> getTwinsItemsFromRecordset(ResultSet rs)
    throws Exception {
        long oldTwinId = -1;
        long twinId;
        int itemType;
        String itemValue;
        Map<Integer, String> twinValues = null;
        Map<Long, Map<Integer, String>> values =
            new HashMap<Long, Map<Integer, String>>();

        while(rs.next()) {
            twinId = rs.getLong(1); // The id is the first
            if (twinId != oldTwinId) {
                //new contact id
                oldTwinId = twinId;
                twinValues = new HashMap<Integer, String>();
                //add new contact record
                values.put(twinId, twinValues);
            }
            //add new item type and value to the map of contact items
            itemType = rs.getByte(2);
            itemValue = rs.getString(3);
            twinValues.put(itemType, itemValue);
        }

        return values;
    }

    /**
     * Analyzes raw list of twins for a contact and returns only the items that
     * satisfies the condition on emails and phone numbers.
     *
     * @param contact the contact to check twins
     * @param twinsInfo the information about potential twins to check
     * @param isUnnamedContact true id the contact is unnamed, false otherwise
     * @return a Map of twin items
     */
    private Map<Long, Map<Integer, String>>
        retrievePotentialTwinsComparingEmailsAndPhoneNumbers(
            Contact contact, 
            Map<Long, Map<Integer, String>> twinsInfo,
            boolean isUnnamedContact) {

        Map<Long, Map<Integer, String>> twinsFound =
            new HashMap<Long, Map<Integer, String>>();

        if (twinsInfo.isEmpty()) return twinsFound;

        //get emails from contact
        List<Email> contactEmails = getContactPropertiesRemovingNullOrEmptyValues(
                contact.getPersonalDetail().getEmails(),
                contact.getBusinessDetail().getEmails());
        List<Email> contactEmailsAux = new ArrayList<Email>();
        for (Email index : contactEmails) {
            index.setPropertyValue(StringUtils.left(index.getPropertyValueAsString(),SQL_EMAIL_DIM));
            contactEmailsAux.add(index);
        }
        contactEmails = contactEmailsAux;

        //get phone numbers from contact
        List<Phone> contactPhones  = getContactPropertiesRemovingNullOrEmptyValues(
                contact.getPersonalDetail().getPhones(),
                contact.getBusinessDetail().getPhones());

        for (Long twinId: twinsInfo.keySet()) {

            Map<Integer, String> twinValues = twinsInfo.get(twinId);

            //get emails for the potential twin
            Map <Integer, String> twinEmails = getPropertiesWithValidValues(
                    twinValues,
                    TYPE_EMAIL_1_ADDRESS,
                    TYPE_EMAIL_2_ADDRESS,
                    TYPE_EMAIL_3_ADDRESS);
            //get phone numbers for the potential twin
            Map <Integer, String> twinPhones = getPropertiesWithValidValues(
                    twinValues,
                    TYPE_ASSISTANT_NUMBER,
                    TYPE_BUSINESS_2_TELEPHONE_NUMBER,
                    TYPE_BUSINESS_FAX_NUMBER,
                    TYPE_BUSINESS_TELEPHONE_NUMBER,
                    TYPE_CALLBACK_NUMBER,
                    TYPE_CAR_TELEPHONE_NUMBER,
                    TYPE_COMPANY_MAIN_TELEPHONE_NUMBER,
                    TYPE_HOME_2_TELEPHONE_NUMBER,
                    TYPE_HOME_FAX_NUMBER,
                    TYPE_HOME_TELEPHONE_NUMBER,
                    TYPE_MOBILE_TELEPHONE_NUMBER,
                    TYPE_OTHER_FAX_NUMBER,
                    TYPE_OTHER_TELEPHONE_NUMBER,
                    TYPE_PAGER_NUMBER,
                    TYPE_PRIMARY_TELEPHONE_NUMBER,
                    TYPE_RADIO_TELEPHONE_NUMBER,
                    TYPE_TELEX_NUMBER);

            boolean areTwins = false;

            //manages the conditions that make the potential twins true twins
            //(inclusion cases)
            if (!isUnnamedContact) {
                //case
                //- if both contact and twins haven't email addresses and phone
                //  numbers, they must be considered twins
                if (!areTwins) {
                    areTwins = contactEmails.isEmpty()
                            && contactPhones.isEmpty()
                            && twinEmails.isEmpty()
                            && twinPhones.isEmpty();
                }
                //case
                //- if the contact contains no fields other than name
                //  (first/last/display/company) and the twin contains name
                //  plus other fields (or viceversa), they must be considered
                // twins
                if (!areTwins) {
                    areTwins = contactOrTwinHasNoFieldsWhileTheOtherHas(
                        contactEmails, contactPhones, twinEmails, twinPhones);
                }
            }

            //case
            //- If they contain at least one identical email address in any
            //  of the address fields
            if (!areTwins) {
                areTwins =
                    haveContactAndTwinEmailsInCommon(contactEmails, twinEmails);
            }
            //case
            //- If they contain at least one identical phone number in any of
            //  the phone number fields
            if (!areTwins) {
                areTwins =
                    haveContactAndTwinPhoneNumbersInCommon(contactPhones, twinPhones);
            }

            //manages the conditions that make the true twins not twins anymore
            //(exclusion cases)

            //manage the case
            //- If the contacts don't have different phone numbers/email
            //  addresses/ in the same corresponding field (e.g. 2 different
            //  phone numbers in the same HOME phone # fields of the contacts)
            if (areTwins) {
                areTwins =
                    haveContactAndTwinSameEmailsInSamePosition(contactEmails, twinEmails);
            }
            if (areTwins) {
                areTwins =
                    haveContactAndTwinSamePhonesInSamePosition(contactPhones, twinPhones);
            }

            if (areTwins){
                twinsFound.put(twinId, twinValues);
            }
        }

        return twinsFound;
    }

    /**
     * Checks if contact and potential twin have emails in common.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     *
     * @return <b>true</b> if contact and twin have at least one email address 
     *         in common, <b>false</b> otherwise
     */
    private boolean haveContactAndTwinEmailsInCommon(
        List<Email> contactEmails,
        Map<Integer, String> twinEmails) {

        //no emails to process
        if (contactEmails.isEmpty() || twinEmails.isEmpty()) return false;

        //aggregate the two email lists
        List<String> emailsSummary = aggregateEmailAddressesRemovingDuplicates(
                contactEmails,
                twinEmails);

        // checks if contact and twin do not share emails or if they share, and
        // if the total number of emails are greater than 3 (prevent data loss)
        if (emailsSummary.size() < (contactEmails.size() + twinEmails.size())
            && emailsSummary.size() <= 3) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if contact and potential twin have phone numbers in common.
     *
     * @param contactPhones the list of contact's phone numbers
     * @param twinPhones the map of twin's phone numbers
     *
     * @return <b>true</b> if contact and twin have at least one phone numbers
     *         in common, <b>false</b> otherwise
     */
    private boolean haveContactAndTwinPhoneNumbersInCommon(
        List<Phone> contactPhones,
        Map<Integer, String> twinPhones) {

        //no phone number to process
        if (contactPhones.isEmpty() || twinPhones.isEmpty()) return false;

        for (Phone phone:contactPhones) {
            String contactPhone = phone.getPropertyValueAsString();
            if (twinPhones.containsValue(contactPhone)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a list that contains all the emails starting from contact and
     * twin lists, but without duplicate elements.
     * The comparison is case insensitive.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     * @return a List of emails
     */
    private List<String> aggregateEmailAddressesRemovingDuplicates(
        List<Email> contactEmails,
        Map<Integer, String> twinEmails) {

        List<String> finalList = new ArrayList<String>();

        for (Email email:contactEmails) {
            finalList.add(email.getPropertyValueAsString());
        }

        for (String twinEmail:twinEmails.values()) {
            boolean foundEmail = false;
            //find if new twin email is a duplicate of an already existing email
            for (Email contactEmail:contactEmails) {
                if (twinEmail.equalsIgnoreCase(contactEmail.getPropertyValueAsString())) {
                    foundEmail = true;
                    break;
                }
            }
            //if it isn't, then add it to the list
            if (!foundEmail) finalList.add(twinEmail);
        }

        return finalList;
    }

    /**
     * Retrieves the map of property that have a not null and not empty value
     * for the given value types.
     *
     * @param values the list of property values to check
     * @param valueTypes the property types to consider
     * @return the list of property values with a valid value.
     */
    private Map<Integer, String> getPropertiesWithValidValues(
        Map<Integer, String> values,
        int... valueTypes) {

        Map<Integer, String> result = new HashMap<Integer, String>();

        for (int type : valueTypes) {
            String value = values.get(type);
            if (StringUtils.isNotEmpty(value)) {
                result.put(type, value);
            }
        }
 
        return result;
    }

    /**
     * Retrieves only the list of contact properties that have a value.
     * The properties with a null or empty value is discarded.
     *
     * @param contactPropertiesLists the list of contact properies to check
     * @return the list of contact's properties with a valid value.
     */
    private List getContactPropertiesRemovingNullOrEmptyValues(
            List... contactPropertiesLists) {
        List<TypifiedProperty> result = new ArrayList();

        for (List<TypifiedProperty> propertiesList : contactPropertiesLists) {
            for (TypifiedProperty property : propertiesList) {
                if (StringUtils.isNotEmpty(property.getPropertyValueAsString())) {
                    result.add(property);
                }
            }
        }
 
        return result;
    }

    /**
     * Verifies that if 
     * - the contact contains no fields other than name, and the twin contains
     *   the same name plus emails and/or phone numbers, they can be considered
     *   twins
     * - the twin contains no fields other than name, and the contact contains
     *   the same name plus emails and/or phone numbers, they can be considered
     *   twins
     *
     * @param contactEmails the list of contact emails
     * @param contactPhones the list of contact phone numbers
     * @param twinEmails the map of the twin's emails
     * @param twinPhones the map of the twin's phone numbers
     * @return true if the conditions are satisfied
     */
    private boolean contactOrTwinHasNoFieldsWhileTheOtherHas(
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Map<Integer, String> twinEmails,
        Map<Integer, String> twinPhones) {

        boolean contactHasEmails = false;
        boolean contactHasPhones = false;
        boolean twinHasEmails = false;
        boolean twinHasPhones = false;

        if (twinEmails != null) twinHasEmails = !twinEmails.isEmpty();
        if (twinPhones != null) twinHasPhones = !twinPhones.isEmpty();

        if (contactEmails != null) contactHasEmails = !contactEmails.isEmpty();
        if (contactPhones != null) contactHasPhones = !contactPhones.isEmpty();

        return  ( (twinHasEmails || twinHasPhones) &&
                  !(contactHasEmails || contactHasPhones) ) ||
                ( (contactHasEmails || contactHasPhones) &&
                  !(twinHasEmails || twinHasPhones) );
    }

    /**
     * Verify that contact and twin have same emails in the same positions.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     * @return true if contact and twin have same emails in the same positions
     */
    private boolean haveContactAndTwinSameEmailsInSamePosition(
            List<Email> contactEmails,
            Map<Integer, String> twinEmails) {

        for (Email email : contactEmails) {
            int type =
                getContactEmailItemTypeFromEmailPropertyType(email.getPropertyType());
            if (TYPE_UNDEFINED == type) continue;
            //get emails
            String emailContactValue = email.getPropertyValueAsString();
            String emailTwinValue = twinEmails.get(type);

            if (StringUtils.isNotEmpty(emailTwinValue)) {
                if (!emailContactValue.equalsIgnoreCase(emailTwinValue))
                    return false;
            }
        }
        return true;
    }

    /**
     * Verify that contact and twin have same phones in the same positions.
     *
     * @param contactPhones the list of contact's phones
     * @param twinPhones the map of twin's phones
     * @return true if contact and twin have same phones in the same positions
     */
    private boolean haveContactAndTwinSamePhonesInSamePosition(
            List<Phone> contactPhones,
            Map<Integer, String> twinPhones) {

        for (Phone phone : contactPhones) {
            int type =
                getContactPhoneItemTypeFromPhonePropertyType(phone.getPropertyType());
            if (TYPE_UNDEFINED == type) continue;
            //get emails
            String phoneContactValue = phone.getPropertyValueAsString();
            String phoneTwinValue = twinPhones.get(type);
            //check them
            if (StringUtils.isNotEmpty(phoneTwinValue)) {
                if (!phoneContactValue.equalsIgnoreCase(phoneTwinValue))
                    return false;
            }
        }
        return true;
    }

    /**
     * Verify if the list of {@link TypifiedProperty} contains at least a valid
     * property value.
     *
     * @param listsOfProperties the list of properties to check
     * @return true if the list contains at least a value
     */
    private boolean hasAtLeastOneNonEmptyProperty(List... listsOfProperties) {
        if (null == listsOfProperties) return false;

        for (List propertiesList : listsOfProperties) {
            if (null == propertiesList) continue;

            //analyze the single list of properties
            for(Object rawProperty : propertiesList) {
                if (rawProperty == null) continue;
                TypifiedProperty prop = (TypifiedProperty) rawProperty;
                String value = prop.getPropertyValueAsString();
                //the property has a non empty value
                if (!StringUtils.isEmpty(normalizeField(value))) return true;
            }
        }

        return false;
    }

    /**
     * Returns the identifier of the given email description type.
     * 
     * @param emailPropertyType the description of the email type
     * @return the identifier of the email type
     */
    private int getContactEmailItemTypeFromEmailPropertyType(String emailPropertyType) {

        int itemType = TYPE_UNDEFINED;

        if ((FIELD_EMAIL_1_ADDRESS).equals(emailPropertyType)) {
            itemType = TYPE_EMAIL_1_ADDRESS;
        } else if ((FIELD_EMAIL_2_ADDRESS).equals(emailPropertyType)) {
            itemType = TYPE_EMAIL_2_ADDRESS;
        } else if ((FIELD_EMAIL_3_ADDRESS).equals(emailPropertyType)) {
            itemType = TYPE_EMAIL_3_ADDRESS;
        } else if ((FIELD_INSTANT_MESSENGER).equals(emailPropertyType)) {
            itemType = TYPE_INSTANT_MESSENGER;
        }

        return itemType;
    }

    /**
     * Returns the identifier of the given phone description type.
     *
     * @param phonePropertyType the description of the phone type
     * @return the identifier of the phone type
     */
    private int getContactPhoneItemTypeFromPhonePropertyType(String phonePropertyType) {

        int itemType = TYPE_UNDEFINED;

        if ((FIELD_HOME_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_HOME_TELEPHONE_NUMBER;
        } else if ((FIELD_HOME_2_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_HOME_2_TELEPHONE_NUMBER;
        } else if ((FIELD_HOME_FAX_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_HOME_FAX_NUMBER;
        } else if ((FIELD_MOBILE_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_MOBILE_TELEPHONE_NUMBER;
        } else if ((FIELD_CAR_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_CAR_TELEPHONE_NUMBER;
        } else if ((FIELD_OTHER_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_OTHER_TELEPHONE_NUMBER;
        } else if ((FIELD_OTHER_FAX_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_OTHER_FAX_NUMBER;
        } else if ((FIELD_PRIMARY_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_PRIMARY_TELEPHONE_NUMBER;
        } else if ((FIELD_BUSINESS_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_BUSINESS_TELEPHONE_NUMBER;
        } else if ((FIELD_BUSINESS_2_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_BUSINESS_2_TELEPHONE_NUMBER;
        } else if ((FIELD_BUSINESS_FAX_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_BUSINESS_FAX_NUMBER;
        } else if ((FIELD_COMPANY_MAIN_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_COMPANY_MAIN_TELEPHONE_NUMBER;
        } else if ((FIELD_PAGER_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_PAGER_NUMBER;
        } else if ((FIELD_ASSISTANT_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_ASSISTANT_NUMBER;
        } else if ((FIELD_CALLBACK_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_CALLBACK_NUMBER;
        } else if ((FIELD_TELEX_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_TELEX_NUMBER;
        } else if ((FIELD_RADIO_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_RADIO_TELEPHONE_NUMBER;
        } else if ((FIELD_TELEX_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_TELEX_NUMBER;
        } else if ((FIELD_RADIO_TELEPHONE_NUMBER).equals(phonePropertyType)) {
            itemType = TYPE_RADIO_TELEPHONE_NUMBER;
        }

        return itemType;
    }

}
