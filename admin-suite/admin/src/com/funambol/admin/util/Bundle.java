/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.util;

import org.openide.util.NbBundle;

/**
 * Wrapper to <code>org.openide.util.NbBundle</code>.
 *
 *
 * @version $Id: Bundle.java,v 1.28 2008-06-30 15:25:50 nichele Exp $
 */
public class Bundle {

    // --------------------------------------------------------------- Constants

    public static final String VERSION = "VERSION";

    public static final String EXPLORER_TITLE = "EXPLORER_TITLE";

    public static final String ERROR_LAUNCH_BROWSER = "ERROR_LAUNCH_BROWSER";
    public static final String URL_DOCUMENTATION = "URL_DOCUMENTATION";

    // nodes
    public static final String LABEL_SERVER_CONFIGURATION  = "LABEL_SERVER_CONFIGURATION";
    public static final String LABEL_SERVER_SETTINGS       = "LABEL_SERVER_SETTINGS"     ;
    public static final String LABEL_LOGGING_SETTINGS      = "LABEL_LOGGING_SETTINGS"    ;
    public static final String LABEL_LOGGERS_SETTINGS      = "LABEL_LOGGERS_SETTINGS"    ;
    public static final String LABEL_APPENDERS_SETTINGS    = "LABEL_APPENDERS_SETTINGS"  ;
    public static final String LABEL_NODE_ROLES            = "LABEL_NODE_ROLES"          ;
    public static final String LABEL_NODE_USERS            = "LABEL_NODE_USERS"          ;
    public static final String LABEL_NODE_DEVICES          = "LABEL_NODE_DEVICES"        ;
    public static final String LABEL_NODE_PRINCIPALS       = "LABEL_NODE_PRINCIPALS"     ;
    public static final String LABEL_NODE_MODULES          = "LABEL_NODE_MODULES"        ;

    // Panels
    public static final String INSERT_USER_PANEL_NAME = "INSERT_USER_PANEL_NAME";
    public static final String EDIT_USER_PANEL_NAME = "EDIT_USER_PANEL_NAME";
    public static final String INSERT_DEVICE_PANEL_NAME = "INSERT_DEVICE_PANEL_NAME";
    public static final String EDIT_DEVICE_PANEL_NAME = "EDIT_DEVICE_PANEL_NAME";
    public static final String INSERT_PRINCIPAL_PANEL_NAME = "INSERT_PRINCIPAL_PANEL_NAME";
    public static final String SEARCH_USER_PANEL_NAME = "SEARCH_USER_PANEL_NAME";
    public static final String SEARCH_DEVICE_PANEL_NAME = "SEARCH_DEVICE_PANEL_NAME";
    public static final String SEARCH_PRINCIPAL_PANEL_NAME = "SEARCH_PRINCIPAL_PANEL_NAME";
    public static final String LAST_TIMESTAMPS_PANEL_NAME = "LAST_TIMESTAMPS_PANEL_NAME";
    public static final String SHOW_SYNCSOURCE_EXCEPTION_TITLE = "SHOW_SYNCSOURCE_EXCEPTION_TITLE";
    public static final String EDIT_LOGGER_PANEL_NAME = "EDIT_LOGGER_PANEL_NAME";
    public static final String EDIT_SERVER_CONFIGURATION_PANEL_NAME = "EDIT_SERVER_CONFIGURATION_PANEL_NAME";
    public static final String CAPABILITIES_DETAILS_PANEL_NAME = "CAPABILITIES_DETAILS_PANEL_NAME";

    // Actions
    public static final String ACTION_ADD_USER_NAME = "ACTION_ADD_USER_NAME";
    public static final String ACTION_SEARCH_USER_NAME = "ACTION_SEARCH_USER_NAME";
    public static final String ACTION_ADD_DEVICE_NAME = "ACTION_ADD_DEVICE_NAME";
    public static final String ACTION_SEARCH_DEVICE_NAME = "ACTION_SEARCH_DEVICE_NAME";
    public static final String ACTION_ADD_PRINCIPAL_NAME = "ACTION_ADD_PRINCIPAL_NAME";
    public static final String ACTION_SEARCH_PRINCIPAL_NAME = "ACTION_SEARCH_PRINCIPAL_NAME";
    public static final String ACTION_DELETE_NAME = "ACTION_DELETE_NAME";
    public static final String ACTION_EDIT_NAME = "ACTION_EDIT_NAME";
    public static final String ACTION_LOGIN_NAME = "ACTION_LOGIN_NAME";
    public static final String ACTION_HELP_CONTENTS = "ACTION_HELP_CONTENTS";
    public static final String ACTION_ABOUT = "ACTION_ABOUT";
    public static final String ACTION_CREATE_SYNC_CONSOLE_NAME = "ACTION_CREATE_SYNC_CONSOLE_NAME";
    public static final String ACTION_ADD_SYNC_SOURCE_NAME = "ACTION_ADD_SYNC_SOURCE_NAME";
    public static final String ACTION_REFRESH = "ACTION_REFRESH";
    public static final String ACTION_EDIT = "ACTION_EDIT";
    public static final String ACTION_EDIT_CONNECTOR = "ACTION_EDIT_CONNECTOR";
    public static final String ACTION_SHOW_OUTPUT_WINDOW = "ACTION_SHOW_OUTPUT_WINDOW";
    public static final String ACTION_OPTIONS = "ACTION_OPTIONS";

    // Button label
    public static final String LABEL_BUTTON_ADD           = "LABEL_BUTTON_ADD"          ;
    public static final String LABEL_BUTTON_SEARCH        = "LABEL_BUTTON_SEARCH"       ;
    public static final String LABEL_BUTTON_SAVE          = "LABEL_BUTTON_SAVE"         ;
    public static final String LABEL_BUTTON_EDIT          = "LABEL_BUTTON_EDIT"         ;
    public static final String LABEL_BUTTON_DELETE        = "LABEL_BUTTON_DELETE"       ;
    public static final String LABEL_BUTTON_SYNC_DETAILS  = "LABEL_BUTTON_SYNC_DETAILS" ;
    public static final String LABEL_BUTTON_LOGIN         = "LABEL_BUTTON_LOGIN"        ;
    public static final String LABEL_BUTTON_CANCEL        = "LABEL_BUTTON_CANCEL"       ;
    public static final String LABEL_BUTTON_ADD_PRINCIPAL = "LABEL_BUTTON_ADD_PRINCIPAL";
    public static final String LABEL_BUTTON_RESET         = "LABEL_BUTTON_RESET"        ;
    public static final String LABEL_BUTTON_REFRESH       = "LABEL_BUTTON_REFRESH"      ;
    public static final String LABEL_BUTTON_CAPABILITIES  = "LABEL_BUTTON_CAPABILITIES" ;
    public static final String LABEL_BUTTON_CONFIGURE     = "LABEL_BUTTON_CONFIGURE"    ;

    // login labels
    public static final String LOGIN_PANEL_TITLE             = "LOGIN_PANEL_TITLE"            ;
    public static final String LOGIN_PANEL_HOST_NAME         = "LOGIN_PANEL_HOST_NAME"        ;
    public static final String LOGIN_PANEL_PORT              = "LOGIN_PANEL_PORT"             ;
    public static final String LOGIN_PANEL_USER              = "LOGIN_PANEL_USER"             ;
    public static final String LOGIN_PANEL_PWD               = "LOGIN_PANEL_PWD"              ;
    public static final String LOGIN_PANEL_PROXY_SETTINGS    = "LOGIN_PANEL_PROXY_SETTINGS"   ;
    public static final String LOGIN_PANEL_USE_PROXY         = "LOGIN_PANEL_USE_PROXY"        ;
    public static final String LOGIN_PANEL_PROXY_PORT        = "LOGIN_PANEL_PROXY_PORT"       ;
    public static final String LOGIN_PANEL_PROXY_USER        = "LOGIN_PANEL_PROXY_USER"       ;
    public static final String LOGIN_PANEL_PROXY_PWD         = "LOGIN_PANEL_PROXY_PWD"        ;
    public static final String LOGIN_PANEL_PROXY_HOST        = "LOGIN_PANEL_PROXY_HOST"       ;
    public static final String LOGIN_PANEL_REMEMBER_PASSWORD = "LOGIN_PANEL_REMEMBER_PASSWORD";
    public static final String LOGIN_PANEL_CONTEXT_PATH      = "LOGIN_PANEL_CONTEXT_PATH"     ;

    // Updates notification labels
    public static final String NEW_DS_SERVER_VERSION_NOTIFICATION_1   = "NEW_DS_SERVER_VERSION_NOTIFICATION_1";
    public static final String NEW_DS_SERVER_VERSION_NOTIFICATION_2   = "NEW_DS_SERVER_VERSION_NOTIFICATION_2";
    public static final String NEW_DS_SERVER_VERSION_NOTIFICATION_URL = "NEW_DS_SERVER_VERSION_NOTIFICATION_URL";
    public static final String SUPPRESS_UPDATES_NOTIFICATION          = "SUPPRESS_UPDATES_NOTIFICATION";
    public static final String UPDATE_NOTIFICATION_PANEL_TITLE        = "UPDATE_NOTIFICATION_PANEL_TITLE";

    // user labels
    public static final String USER_PANEL_ROLES        = "USER_PANEL_ROLES"       ;
    public static final String USER_PANEL_USERNAME     = "USER_PANEL_USERNAME"    ;
    public static final String USER_PANEL_FIRSTNAME    = "USER_PANEL_FIRSTNAME"   ;
    public static final String USER_PANEL_LASTNAME     = "USER_PANEL_LASTNAME"    ;
    public static final String USER_PANEL_PASSWORD     = "USER_PANEL_PASSWORD"    ;
    public static final String USER_PANEL_CONFPASSWORD = "USER_PANEL_CONFPASSWORD";
    public static final String USER_PANEL_EMAIL        = "USER_PANEL_EMAIL"       ;
    public static final String USER_PANEL_NAME         = "USER_PANEL_NAME"        ;

    // user questions
    public static final String USER_QUESTION_DELETE = "USER_QUESTION_DELETE";
    public static final String USER_DELETE_NOT_ALLOWED = "USER_DELETE_NOT_ALLOWED";
    public static final String USER_QUESTION_UPDATE = "USER_QUESTION_UPDATE";

    // user messages
    public static final String USER_MESSAGE_USERNAME_EXISTS = "USER_MESSAGE_USERNAME_EXISTS";
    public static final String USER_MESSAGE_USERS_FOUND     = "USER_MESSAGE_USERS_FOUND"    ;
    public static final String USER_MESSAGE_USER_FOUND      = "USER_MESSAGE_USER_FOUND"     ;
    public static final String USER_MESSAGE_USER_NOT_FOUND  = "USER_MESSAGE_USER_NOT_FOUND" ;
    public static final String USER_MESSAGE_EDIT_OK         = "USER_MESSAGE_EDIT_OK"        ;
    public static final String USER_MESSAGE_CHECK_ROLES     = "USER_MESSAGE_CHECK_ROLES"    ;
    public static final String USER_MESSAGE_INSERT_OK       = "USER_MESSAGE_INSERT_OK"      ;
    public static final String USER_MESSAGE_DELETE_OK       = "USER_MESSAGE_DELETE_OK"      ;
    public static final String USER_MESSAGE_CHECK_PASSWORD  = "USER_MESSAGE_CHECK_PASSWORD" ;

    // device labels
    public static final String DEVICE_PANEL_ID            = "DEVICE_PANEL_ID"           ;
    public static final String DEVICE_PANEL_TYPE          = "DEVICE_PANEL_TYPE"         ;
    public static final String DEVICE_PANEL_TIMEZONE      = "DEVICE_PANEL_TIMEZONE"     ;
    public static final String DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ =
            "DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ";
    public static final String DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED =
            "DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED";
    public static final String DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED_DEFAULT =
            "DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED_DEFAULT";
    public static final String DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED =
            "DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED";
    public static final String DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED_DEFAULT =
            "DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED_DEFAULT";
    public static final String DEVICE_PANEL_CHARSET       = "DEVICE_PANEL_CHARSET"      ;
    public static final String DEVICE_PANEL_ADDRESS       = "DEVICE_PANEL_ADDRESS"      ;
    public static final String DEVICE_PANEL_MSISDN        = "DEVICE_PANEL_MSISDN"       ;
    public static final String DEVICE_PANEL_NOTIFICATION_BUILDER =
        "DEVICE_PANEL_NOTIFICATION_BUILDER";
    public static final String DEVICE_PANEL_NOTIFICATION_SENDER  =
        "DEVICE_PANEL_NOTIFICATION_SENDER";
    public static String DEVICE_PANEL_NOTIFICATION_BUILDER_11      = "DEVICE_PANEL_NOTIFICATION_BUILDER_11";
    public static String DEVICE_PANEL_NOTIFICATION_BUILDER_12      = "DEVICE_PANEL_NOTIFICATION_BUILDER_12";
    public static String DEVICE_PANEL_NOTIFICATION_PUSH_SENDER     = "DEVICE_PANEL_NOTIFICATION_PUSH_SENDER";

    public static final String DEVICE_PANEL_DESCRIPTION   = "DEVICE_PANEL_DESCRIPTION"  ;

    // device questions
    public static final String DEVICE_QUESTION_DELETE = "DEVICE_QUESTION_DELETE";
    public static final String DEVICE_QUESTION_UPDATE = "DEVICE_QUESTION_UPDATE";

    // device messages
    public static final String DEVICE_MESSAGE_INSERT_OK        = "DEVICE_MESSAGE_INSERT_OK"       ;
    public static final String DEVICE_MESSAGE_ID_EXISTS        = "DEVICE_MESSAGE_ID_EXISTS"       ;
    public static final String DEVICE_MESSAGE_SAVE_OK          = "DEVICE_MESSAGE_SAVE_OK"         ;
    public static final String DEVICE_MESSAGE_DELETE_OK        = "DEVICE_MESSAGE_DELETE_OK"       ;
    public static final String DEVICE_MESSAGE_DEVICES_FOUND    = "DEVICE_MESSAGE_DEVICES_FOUND"   ;
    public static final String DEVICE_MESSAGE_DEVICE_FOUND     = "DEVICE_MESSAGE_DEVICE_FOUND"    ;
    public static final String DEVICE_MESSAGE_DEVICE_NOT_FOUND = "DEVICE_MESSAGE_DEVICE_NOT_FOUND";

    //capabilities messages
    public static final String CAPABILITIES_MESSAGE_INSERT_OK = "CAPABILITIES_MESSAGE_INSERT_OK";

    // principal labels
    public static final String PRINCIPAL_PANEL_USERNAME    = "PRINCIPAL_PANEL_USERNAME"   ;
    public static final String PRINCIPAL_PANEL_DEVICEID    = "PRINCIPAL_PANEL_DEVICEID"   ;
    public static final String PRINCIPAL_PANEL_PRINCIPALID = "PRINCIPAL_PANEL_PRINCIPALID";

    // principal questions
    public static final String PRINCIPAL_QUESTION_DELETE = "PRINCIPAL_QUESTION_DELETE";

    // principal messages
    public static final String PRINCIPAL_MESSAGE_INSERT_OK = "PRINCIPAL_MESSAGE_INSERT_OK";
    public static final String PRINCIPAL_MESSAGE_DELETE_OK = "PRINCIPAL_MESSAGE_DELETE_OK";

    public static final String PRINCIPAL_MESSAGE_PRINCIPALS_FOUND =
        "PRINCIPAL_MESSAGE_PRINCIPALS_FOUND";
    public static final String PRINCIPAL_MESSAGE_PRINCIPAL_FOUND =
        "PRINCIPAL_MESSAGE_PRINCIPAL_FOUND";
    public static final String PRINCIPAL_MESSAGE_PRINCIPAL_NOT_FOUND =
        "PRINCIPAL_MESSAGE_PRINCIPAL_NOT_FOUND";

    // principal labels
    public static final String LAST_TIMESTAMPS_PANEL_PRINCIPAL = "LAST_TIMESTAMPS_PANEL_PRINCIPAL";
    public static final String LAST_TIMESTAMPS_PANEL_DATABASE = "LAST_TIMESTAMPS_PANEL_DATABASE";
    public static final String LAST_TIMESTAMPS_PANEL_SYNC_TYPE  = "LAST_TIMESTAMPS_PANEL_SYNC_TYPE" ;
    public static final String LAST_TIMESTAMPS_PANEL_STATUS     = "LAST_TIMESTAMPS_PANEL_STATUS"    ;
    public static final String LAST_TIMESTAMPS_PANEL_TAG_CLIENT = "LAST_TIMESTAMPS_PANEL_TAG_CLIENT";
    public static final String LAST_TIMESTAMPS_PANEL_TAG_SERVER = "LAST_TIMESTAMPS_PANEL_TAG_SERVER";
    public static final String LAST_TIMESTAMPS_PANEL_START = "LAST_TIMESTAMPS_PANEL_START";
    public static final String LAST_TIMESTAMPS_PANEL_END = "LAST_TIMESTAMPS_PANEL_END";

    // last timestamp questions
    public static final String LAST_TIMESTAMP_QUESTION_RESET = "LAST_TIMESTAMP_QUESTION_RESET";

    // last timestamp messages
    public static final String LAST_TIMESTAMP_MESSAGE_RESET_OK = "LAST_TIMESTAMP_MESSAGE_RESET_OK";
    public static final String LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMPS_FOUND =
        "LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMPS_FOUND";
    public static final String LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_FOUND =
        "LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_FOUND";
    public static final String LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_NOT_FOUND =
        "LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_NOT_FOUND";

    // syncSource labels
    public static final String SYNC_SOURCE_EXCEPTION_NO_MESSAGE_AVAILABLE =
        "SYNCSOURCE_EXCEPTION_NO_MESSAGE_AVAILABLE";
    public static final String LABEL_SYNC_SOURCE_URI = "LABEL_SYNC_SOURCE_URI";
    public static final String LABEL_SYNC_SOURCE_NAME = "LABEL_SYNC_SOURCE_NAME";
    public static final String LABEL_SYNC_SOURCE_SUPPORTED_TYPES = "LABEL_SYNC_SOURCE_SUPPORTED_TYPES";
    public static final String LABEL_SYNC_SOURCE_SUPPORTED_VERSIONS = "LABEL_SYNC_SOURCE_SUPPORTED_VERSIONS";
    public static final String LABEL_SYNC_SOURCE_ENCRYPTION = "LABEL_SYNC_SOURCE_ENCRYPTION";
    public static final String LABEL_SYNC_SOURCE_ENCODING = "LABEL_SYNC_SOURCE_ENCODING";
    public static final String LABEL_SYNC_SOURCE_EDIT = "LABEL_SYNC_SOURCE_EDIT";

    // syncSource questions
    public static final String SYNC_SOURCE_QUESTION_DELETE = "SYNC_SOURCE_QUESTION_DELETE";

    // syncSource messages
    public static final String SYNC_SOURCE_MESSAGE_INSERT_OK  = "SYNC_SOURCE_MESSAGE_INSERT_OK";
    public static final String SYNC_SOURCE_MESSAGE_UPDATED_OK = "SYNC_SOURCE_MESSAGE_UPDATED_OK";
    public static final String SYNC_SOURCE_MESSAGE_DELETED_OK = "SYNC_SOURCE_MESSAGE_DELETED_OK";
    public static final String SYNC_SOURCE_MESSAGE_URI_OR_NAME_EXISTS =
        "SYNC_SOURCE_MESSAGE_URI_OR_NAME_EXISTS";
    public static final String SYNC_SOURCE_MESSAGE_NAME_EXISTS =
        "SYNC_SOURCE_MESSAGE_NAME_EXISTS";
    public static final String SYNC_SOURCE_MESSAGE_NOT_CONFIGURABLE =
        "SYNC_SOURCE_MESSAGE_NOT_CONFIGURABLE";
    public static final String SYNC_SOURCE_MESSAGE_INSERT_NOT_CONFIGURABLE =
        "SYNC_SOURCE_MESSAGE_INSERT_NOT_CONFIGURABLE";

    // server settings
    public static final String LABEL_DEVICE_INVENTORY          = "LABEL_DEVICE_INVENTORY"         ;
    public static final String LABEL_DATA_TRANSFORMER_MANAGER  = "LABEL_DATA_TRANSFORMER_MANAGER" ;
    public static final String LABEL_ENGINE_INFO               = "LABEL_ENGINE_INFO"              ;
    public static final String LABEL_SERVER_URI                = "LABEL_SERVER_URI"               ;
    public static final String LABEL_OFFICER                   = "LABEL_OFFICER"                  ;
    public static final String LABEL_LOGGING_CONFIGURATION     = "LABEL_LOGGING_CONFIGURATION"    ;
    public static final String LABEL_PIPELINE_MANAGER          = "LABEL_PIPELINE_MANAGER"         ;
    public static final String LABEL_HANDLER                   = "LABEL_HANDLER"                  ;
    public static final String LABEL_PERSISTENCE_STORE_MANAGER = "LABEL_PERSISTENCE_STORE_MANAGER";
    public static final String LABEL_STRATEGY                  = "LABEL_STRATEGY"                 ;
    public static final String LABEL_USER_MANAGER              = "LABEL_USER_MANAGER"             ;
    public static final String LABEL_SMS_SERVICE               = "LABEL_SMS_SERVICE"              ;
    public static final String LABEL_MIN_MAX_MSG_SIZE          = "LABEL_MIN_MAX_MSG_SIZE"         ;
    public static final String LABEL_CHECK_FOR_UPDATES         = "LABEL_CHECK_FOR_UPDATES"        ;
    public static final String LABEL_DEV_INF                   = "LABEL_DEV_INF"                  ;
    public static final String LABEL_DEV_ID                    = "LABEL_DEV_ID"                   ;
    public static final String LABEL_DEV_TYP                   = "LABEL_DEV_TYP"                  ;
    public static final String LABEL_MAN                       = "LABEL_MAN"                      ;
    public static final String LABEL_MOD                       = "LABEL_MOD"                      ;
    public static final String LABEL_SWV                       = "LABEL_SWV"                      ;
    public static final String LABEL_HWV                       = "LABEL_HWV"                      ;
    public static final String LABEL_FWV                       = "LABEL_FWV"                      ;
    public static final String LABEL_OEM                       = "LABEL_OEM"                      ;
    public static final String LABEL_VER_DTD                   = "LABEL_VER_DTD"                  ;
    public static final String LABEL_SUPPORT_LARGE_OBJECT      = "LABEL_SUPPORT_LARGE_OBJECT"     ;
    public static final String LABEL_SUPPORT_NUMBER_OF_CHANGES = "LABEL_SUPPORT_NUMBER_OF_CHANGES";
    public static final String LABEL_UTC                       = "LABEL_UTC"                      ;

    public static final String MSG_SERVER_CONFIGURATION_SETTINGS_SAVED = "MSG_SERVER_CONFIGURATION_SETTINGS_SAVED";

    // generic error messages
    public static final String CONNECTION_ERROR_MESSAGE = "CONNECTION_ERROR_MESSAGE";

    public static final String CONNECT_EXCEPTION_ERROR_MESSAGE = "CONNECT_EXCEPTION_ERROR_MESSAGE";

    public static final String AUTHENTICATION_FAILED_ERROR_MESSAGE =
        "AUTHENTICATION_FAILED_ERROR_MESSAGE";

    public static final String AUTHENTICATION_FAILED_DATA_STORE =
        "AUTHENTICATION_FAILED_DATA_STORE";

    public static final String ERROR                                = "ERROR"                     ;
    public static final String ERROR_LOADING_CONFIG_PANEL           = "ERROR_LOADING_CONFIG_PANEL";
    public static final String ERROR_LOADING_CONNECTOR_CONFIG_PANEL = "ERROR_LOADING_CONNECTOR_CONFIG_PANEL";

    public static final String ERROR_CREATING             = "ERROR_CREATING"            ;
    public static final String ERROR_HANDLING_PRINCIPAL   = "ERROR_HANDLING_PRINCIPAL"  ;
    public static final String ERROR_HANDLING_LAST_TIMESTAMP
                                                          = "ERROR_HANDLING_LAST_TIMESTAMP"  ;
    public static final String ERROR_HANDLING_DEVICE      = "ERROR_HANDLING_DEVICE"     ;
    public static final String ERROR_HANDLING_USER        = "ERROR_HANDLING_USER"       ;
    public static final String ERROR_HANDLING_SYNCSOURCE  = "ERROR_HANDLING_SYNCSOURCE" ;
    public static final String ERROR_READING_MODULES      = "ERROR_READING_MODULES"     ;
    public static final String ERROR_LOADING_SYNCSOURCE_TYPE
                                                          = "ERROR_LOADING_SYNCSOURCE_TYPE";
    public static final String ERROR_SAVING_SERVER_CONFIGURATION
                                                          = "ERROR_SAVING_SERVER_CONFIGURATION";
    public static final String ERROR_LOADING_SERVER_CONFIGURATION
                                                          = "ERROR_LOADING_SERVER_CONFIGURATION";

    public static final String ERROR_LOADING_SERVER_LOGGERS
                                                          = "ERROR_LOADING_SERVER_LOGGERS";

    public static final String ERROR_LOADING_SERVER_APPENDERS
                                                          = "ERROR_LOADING_SERVER_APPENDERS";

    public static final String ERROR_NUMERIC_INPUT        = "ERROR_NUMERIC_INPUT"       ;
    public static final String ERROR_EMPTY_INPUT          = "ERROR_EMPTY_INPUT"         ;
    public static final String ERROR_SERVER_URI_NOT_VALID = "ERROR_SERVER_URI_NOT_VALID";
    public static final String UNEXPECTED_ERROR           = "UNEXPECTED_ERROR"          ;
    public static final String UNEXPECTED_SERVER_ERROR    = "UNEXPECTED_SERVER_ERROR"   ;


    public static final String ERROR_INVALID_ACTION_EVENT = "ERROR_INVALID_ACTION_EVENT";

    // warnings
    public static final String WARNING_SERVER_VERSION     = "WARNING_SERVER_VERSION";


    public static final String INPUT_DATA_ERROR_MESSAGE = "INPUT_DATA_ERROR_MESSAGE";

    public static final String NO_DATA_SELECTED = "NO_DATA_SELECTED";

    public static final String MESSAGE_MAX_NUMBER_OF_RESULTS = "MESSAGE_MAX_NUMBER_OF_RESULTS";

    public static final String SYNC_WORKSPACE_NAME = "SYNC_WORKSPACE_NAME";

    public static final String DOUBLE_CLICK_TO_OPEN = "DOUBLE_CLICK_TO_OPEN";

    public static final String UPDATING  = "UPDATING" ;
    public static final String DELETING  = "DELETING" ;
    public static final String SEARCHING = "SEARCHING";
    public static final String EDITING   = "EDITING"  ;
    public static final String ADDING    = "ADDING"   ;

    public static final String LABEL_LOGLEVEL_OFF   = "LABEL_LOGLEVEL_OFF"  ;
    public static final String LABEL_LOGLEVEL_FATAL = "LABEL_LOGLEVEL_FATAL";
    public static final String LABEL_LOGLEVEL_ERROR = "LABEL_LOGLEVEL_ERROR";
    public static final String LABEL_LOGLEVEL_WARN  = "LABEL_LOGLEVEL_WARN" ;
    public static final String LABEL_LOGLEVEL_INFO  = "LABEL_LOGLEVEL_INFO" ;
    public static final String LABEL_LOGLEVEL_DEBUG = "LABEL_LOGLEVEL_DEBUG";
    public static final String LABEL_LOGLEVEL_TRACE = "LABEL_LOGLEVEL_TRACE";
    public static final String LABEL_LOGLEVEL_ALL   = "LABEL_LOGLEVEL_ALL"  ;

    public static final String LABEL_LOGGER_NAME          = "LABEL_LOGGER_NAME"         ;
    public static final String LABEL_LOGGER_INHERIT       = "LABEL_LOGGER_INHERIT"      ;
    public static final String LABEL_LOGGER_LEVEL         = "LABEL_LOGGER_LEVEL"        ;
    public static final String LABEL_APPENDERS            = "LABEL_APPENDERS"           ;
    public static final String LABEL_USERS_WITH_LEVEL_ALL = "LABEL_USERS_WITH_LEVEL_ALL";

    public static final String LABEL_APPENDER_NAME        = "LABEL_APPENDER_NAME"       ;
    public static final String LABEL_PATTERN_LAYOUT       = "LABEL_PATTERN_LAYOUT"      ;
    public static final String LABEL_MAX_FILE_SIZE        = "LABEL_MAX_FILE_SIZE"       ;
    public static final String LABEL_MAX_BACKUP_INDEX     = "LABEL_MAX_BACKUP_INDEX"    ;
    public static final String LABEL_FILE_NAME            = "LABEL_FILE_NAME"           ;
    public static final String LABEL_MB                   = "LABEL_MB"                  ;
    public static final String LABEL_DATE_PATTERN         = "LABEL_DATE_PATTERN"        ;
    public static final String LABEL_FILE_APPENDER_NOTE   = "LABEL_FILE_APPENDER_NOTE"  ;
    public static final String LABEL_DEFAULT_APPENDER_SETTINGS =
        "LABEL_DEFAULT_APPENDER_SETTINGS";

    public static final String MSG_APPENDER_NOT_CONFIGURABLE =
        "MSG_APPENDER_NOT_CONFIGURABLE";

    public static final String LABEL_LOGGER_SYSTEM_CONFIGURATION =
        "LABEL_LOGGER_SYSTEM_CONFIGURATION";

    public static final String LABEL_MESSAGES_TAB =
        "LABEL_MESSAGES_TAB";

    public static final String MSG_LOGGING_SETTINGS_SAVED =
        "MSG_LOGGING_SETTINGS_SAVED";

    public static final String MSG_LOGGER_CONFIGURATION_SAVED =
        "MSG_LOGGER_CONFIGURATION_SAVED";

    public static final String ERROR_SAVING_LOGGER_CONFIGURATION =
        "ERROR_SAVING_LOGGER_CONFIGURATION";

    public static final String ERROR_LOADING_APPENDER_CONFIG_PANEL =
        "ERROR_LOADING_APPENDER_CONFIG_PANEL";

    public static final String EDIT_CONSOLE_APPENDER_PANEL_NAME =
        "EDIT_CONSOLE_APPENDER_PANEL_NAME";

    public static final String EDIT_ROLLING_FILE_APPENDER_PANEL_NAME =
        "EDIT_ROLLING_FILE_APPENDER_PANEL_NAME";

    public static final String EDIT_DAILY_ROLLING_FILE_APPENDER_PANEL_NAME =
        "EDIT_DAILY_ROLLING_FILE_APPENDER_PANEL_NAME";


    //labels for DataTransformerManager panel
    public static final String DATA_TRANSFORMERS_MANAGER_PANEL_NAME =
        "DATA_TRANSFORMERS_MANAGER_PANEL_NAME";
    public static final String DATA_TRANSFORMER_CONFIGURE_BUTTON =
        "DATA_TRANSFORMER_CONFIGURE_BUTTON";
    public static final String ERROR_GETTING_DTM_CONFIGURATION =
        "ERROR_GETING_DTM_CONFIGURATION";
    public static final String ERROR_SAVING_DTM_CONFIGURATION =
        "ERROR_SAVING_DTM_CONFIGURATION";
     public static final String MSG_DTM_CONFIGURATION_SAVED =
         "MSG_DTM_CONFIGURATION_SAVED";
    public static final String DTM_LABEL_TITLE            = "DTM_LABEL_TITLE";
    public static final String DTM_LABEL_INCOMING         = "DTM_LABEL_INCOMING";
    public static final String DTM_COL_NAME               = "DTM_COL_NAME";
    public static final String DTM_COL_CLASS              = "DTM_COL_CLASS";
    public static final String DTM_LABEL_OUTGOING         = "DTM_LABEL_OUTGOING";
    public static final String DTM_COL_SOURCE_URI         = "DTM_COL_SOURCE_URI";
    public static final String DTM_COL_TRANSFORMATION     = "DTM_COL_TRANSFORMATION";
    public static final String DTM_LABEL_TRANSF_REQUIRED  = "DTM_LABEL_TRANSF_REQUIRED";
    public static final String DTM_BUTTON_SAVE            = "DTM_BUTTON_SAVE";
    public static final String DTM_NEW_ITEM               = "DTM_NEW_ITEM";
    public static final String DTM_MSG_DELETE             = "DTM_MSG_DELETE";
    public static final String DTM_MSG_DELETE_TITLE       = "DTM_MSG_DELETE_TITLE";

    //labels for Sync4jStrategy panel
    public static final String ERROR_GETTING_STRATEGY_CONFIGURATION = "ERROR_GETTING_STRATEGY_CONFIGURATION";
    public static final String SYNC4JSTRATEGY_PANEL_NAME            = "SYNC4JSTRATEGY_PANEL_NAME";
    public static final String STRATEGY_PANEL_TITLE                 = "STRATEGY_PANEL_TITLE";
    public static final String STRATEGY_LABEL_CONFLICT_RESOLUTION   = "STRATEGY_LABEL_CONFLICT_RESOLUTION";
    public static final String STRATEGY_LABEL_DEFAULT_CONFLICT      = "STRATEGY_LABEL_DEFAULT_CONFLICT";
    public static final String STRATEGY_OPTION_SERVER_WINS          = "STRATEGY_OPTION_SERVER_WINS";
    public static final String STRATEGY_OPTION_CLIENT_WINS          = "STRATEGY_OPTION_CLIENT_WINS";
    public static final String STRATEGY_OPTION_DEFAULT_VALUE        = "STRATEGY_OPTION_DEFAULT_VALUE";
    public static final String STRATEGY_OPTION_MERGE_DATA           = "STRATEGY_OPTION_MERGE_DATA";
    public static final String STRATEGY_NOTE                        = "STRATEGY_NOTE";
    public static final String STRATEGY_COL_NAME                    = "STRATEGY_COL_NAME";
    public static final String STRATEGY_COL_RESOLUTION              = "STRATEGY_COL_RESOLUTION";
    public static final String STRATEGY_BUTTON_SAVE                 = "STRATEGY_BUTTON_SAVE";
    public static final String ERROR_GETTING_SYNCSOURCES            = "ERROR_GETTING_SYNCSOURCES";
    public static final String MSG_STRATEGY_CONFIGURATION_SAVED     = "MSG_STRATEGY_CONFIGURATION_SAVED";
    public static final String ERROR_SAVING_STRATEGY_CONFIGURATION  = "ERROR_SAVING_STRATEGY_CONFIGURATION";
    public static final String STRATEGY_NEW_URI                     = "STRATEGY_NEW_URI";
    public static final String STRATEGY_NEW_RESOLUTION              = "STRATEGY_NEW_RESOLUTION";
    public static final String STRATEGY_MSG_DELETE                  = "STRATEGY_MSG_DELETE";
    public static final String STRATEGY_MSG_DELETE_TITLE            = "STRATEGY_MSG_DELETE_TITLE";

    //labels for Sync4jStrategy panel
    public static final String ABOUT_COPYRIGHT  = "ABOUT_COPYRIGHT" ;
    public static final String ABOUT_URL        = "ABOUT_URL"       ;
    public static final String ABOUT_LICENSE    = "ABOUT_LICENSE"   ;
    public static final String ABOUT_PANEL_NAME = "ABOUT_PANEL_NAME";

    //options panel
    public static final String OPTIONS_PANEL_CONTEXT_PATH =
        "OPTIONS_PANEL_CONTEXT_PATH";
    public static final String OPTIONS_PANEL_MAX_RESULTS  =
        "OPTIONS_PANEL_MAX_RESULTS";
    public static final String OPTIONS_PANEL_DEBUG = "OPTIONS_PANEL_DEBUG";
    public static final String OPTIONS_PANEL_NOTE  = "OPTIONS_PANEL_NOTE" ;
    public static final String OPTIONS_PANEL_NAME  = "OPTIONS_PANEL_NAME" ;

    // ------------------------------------------------------------ Private data
    private static final java.util.ResourceBundle rb = NbBundle.getBundle(Bundle.class);

    // ---------------------------------------------------------- Public Methods
    /**
     * Finds a localized string in the bundle.
     *
     * @param s name of the resource to look for
     * @return the string associated with the resource
     */
    public static final String getMessage(String s) {
        return rb.getString(s);
    }
}
