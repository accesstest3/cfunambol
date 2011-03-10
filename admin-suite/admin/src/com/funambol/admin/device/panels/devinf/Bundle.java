/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.admin.device.panels.devinf;

import java.util.ResourceBundle;

import org.openide.util.NbBundle;

/**
 * Class used to read the resources for the capabiltiesFrame
 *
 * @version $Id: Bundle.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class Bundle {

    // --------------------------------------------------------------- Constants

    //panel title/name
    public static final String CAPABILITIES_DETAILS_PANEL_NAME = "CAPABILITIES_DETAILS_PANEL_NAME";

    //labels
    public static final String LABEL_DEVICE_CAPABILITIES    = "LABEL_DEVICE_CAPABILITIES";
    public static final String LABEL_DEVICE_ID              = "LABEL_DEVICE_ID";
    public static final String LABEL_PROPERTIES             = "LABEL_PROPERTIES";
    public static final String LABEL_DATASTORES             = "LABEL_DATASTORES";
    public static final String LABEL_EXT                    = "LABEL_EXT";
    public static final String LABEL_DATASTORE              = "LABEL_DATASTORE";
    public static final String LABEL_SYNCCAP                = "LABEL_SYNCCAP";
    public static final String LABEL_DSMEM                  = "LABEL_DSMEM";
    public static final String LABEL_CTCAP                  = "LABEL_CTCAP";
    public static final String LABEL_VAL_ENUM               = "LABEL_VAL_ENUM";
    public static final String LABEL_PARAMETERS             = "LABEL_PARAMETERS";
    public static final String LABEL_CTCAP_PROPERTIES       = "LABEL_CTCAP_PROPERTIES";
    public static final String LABEL_TYPE                   = "LABEL_TYPE";
    public static final String LABEL_VERSION                = "LABEL_VERSION";
    public static final String LABEL_FILTER_CAP_PROPERTIES  = "LABEL_FILTER_CAP_PROPERTIES";
    public static final String LABEL_KEYWORDS               = "LABEL_KEYWORDS";
    public static final String LABEL_EXT_PROPERTIES         = "LABEL_EXT_PROPERTIES";
    public static final String LABEL_EXT_NAME               = "LABEL_EXT_NAME";
    public static final String LABEL_RX                     = "LABEL_RX";
    public static final String LABEL_TX                     = "LABEL_TX";
    public static final String LABEL_FILTER_RX              = "LABEL_FILTER_RX";
    public static final String LABEL_FILTER_CAP             = "LABEL_FILTER_CAP";

    //buttons
    public static final String BUTTON_SAVE                  = "BUTTON_SAVE";
    public static final String BUTTON_RESET                 = "BUTTON_RESET";
    public static final String BUTTON_DISCARD               = "BUTTON_DISCARD";
    public static final String BUTTON_OK                    = "BUTTON_OK";

    //columns name
    public static final String COL_PEOPERTY                 = "COL_PEOPERTY";
    public static final String COL_VALUE                    = "COL_VALUE";
    public static final String COL_LABEL                    = "COL_LABEL";
    public static final String COL_XNAME                    = "COL_XNAME";
    public static final String COL_XVALUES                  = "COL_XVALUES";
    public static final String COL_TYPE                     = "COL_TYPE";
    public static final String COL_VERSION                  = "COL_VERSION";
    public static final String COL_FIELD_LEVEL              = "COL_FIELD_LEVEL";
    public static final String COL_PROPERTIES               = "COL_PROPERTIES";
    public static final String COL_PREFERRED                = "COL_PREFERRED";
    //columns for CTCAP properties
    public static final String COL_PROP_NAME                = "COL_PROP_NAME";
    public static final String COL_DISPLAY_NAME             = "COL_DISPLAY_NAME";
    public static final String COL_DATA_TYPE                = "COL_DATA_TYPE";
    public static final String COL_MAX_OCCUR                = "COL_MAX_OCCUR";
    public static final String COL_MAX_SIZE                 = "COL_MAX_SIZE";
    public static final String COL_NO_TRUNCATE              = "COL_NO_TRUNCATE";
    public static final String COL_PARAM_NAME               = "COL_PARAM_NAME";

    //messages
    public static final String MSG_PROP_NAME_EMPTY          = "MSG_PROP_NAME_EMPTY";
    public static final String MSG_INVALID_MAX_OCCUR        = "MSG_INVALID_MAX_OCCUR";
    public static final String MSG_INVALID_MAX_SIZE         = "MSG_INVALID_MAX_SIZE";
    public static final String MSG_VERSION_EMPTY            = "MSG_VERSION_EMPTY";
    public static final String MSG_PARAM_NAME_EMPTY         = "MSG_PARAM_NAME_EMPTY";
    public static final String MSG_VALUE_EMPTY              = "MSG_VALUE_EMPTY";
    public static final String MSG_TYPE_EMPTY               = "MSG_TYPE_EMPTY";
    public static final String MSG_SOURCEREF_EMPTY          = "MSG_SOURCEREF_EMPTY";
    public static final String MSG_LABEL_EMPTY              = "MSG_LABEL_EMPTY";
    public static final String MSG_INVALID_MAX_GUID         = "MSG_INVALID_MAX_GUID";
    public static final String MSG_INVALID_MAX_MEM          = "MSG_INVALID_MAX_MEM";
    public static final String MSG_INVALID_MAX_ID           = "MSG_INVALID_MAX_ID";
    public static final String MSG_EXT_ALREADY_EXISTS       = "MSG_EXT_ALREADY_EXISTS";
    public static final String MSG_EXT_EMPTY                = "MSG_EXT_EMPTY";
    public static final String MSG_KEYWORD_EMPTY            = "MSG_KEYWORD_EMPTY";
    public static final String MSG_FILTER_CAP_ALREADY_EXISTS = "MSG_FILTER_CAP_ALREADY_EXISTS";
    public static final String MSG_FILTER_RX_ALREADY_EXISTS = "MSG_FILTER_RX_ALREADY_EXISTS";
    public static final String MSG_RX_ALREADY_EXISTS        = "MSG_RX_ALREADY_EXISTS";
    public static final String MSG_TX_ALREADY_EXISTS        = "MSG_TX_ALREADY_EXISTS";
    public static final String MSG_TYPE_VERSION_ALREADY_EXISTS_1 = "MSG_TYPE_VERSION_ALREADY_EXISTS_1";
    public static final String MSG_TYPE_VERSION_ALREADY_EXISTS_2 = "MSG_TYPE_VERSION_ALREADY_EXISTS_2";
    public static final String MSG_RX_PREFERRED_DELETE      = "MSG_RX_PREFERRED_DELETE";
    public static final String MSG_TX_PREFERRED_DELETE      = "MSG_TX_PREFERRED_DELETE";
    public static final String MSG_INVALID_SYNC_CAP         = "MSG_INVALID_SYNC_CAP";
    public static final String MSG_SYNC_CAP_EMPTY           = "MSG_SYNC_CAP_EMPTY";
    public static final String MSG_CTCAPS_PROP_DELETE_ALL   = "MSG_CTCAPS_PROP_DELETE_ALL";
    public static final String MSG_SYNCCAP_DELETE_ALL       = "MSG_SYNCCAP_DELETE_ALL";
    public static final String MSG_CTCAP_DELETE_ALL         = "MSG_CTCAP_DELETE_ALL";
    //confirmation messages for delete
    public static final String MSG_DELETE_DATASTORE         = "MSG_DELETE_DATASTORE";
    public static final String MSG_DELETE_DATASTORE_TITLE   = "MSG_DELETE_DATASTORE_TITLE";
    public static final String MSG_DELETE_EXT               = "MSG_DELETE_EXT";
    public static final String MSG_DELETE_EXT_TITLE         = "MSG_DELETE_EXT_TITLE";
    public static final String MSG_DELETE_SYNCCAP           = "MSG_DELETE_SYNCCAP";
    public static final String MSG_DELETE_SYNCCAP_TITLE     = "MSG_DELETE_SYNCCAP_TITLE";
    public static final String MSG_DELETE_FILTERRX          = "MSG_DELETE_FILTERRX";
    public static final String MSG_DELETE_FILTERRX_TITLE    = "MSG_DELETE_FILTERRX_TITLE";
    public static final String MSG_DELETE_RX                = "MSG_DELETE_RX";
    public static final String MSG_DELETE_RX_TITLE          = "MSG_DELETE_RX_TITLE";
    public static final String MSG_DELETE_TX                = "MSG_DELETE_TX";
    public static final String MSG_DELETE_TX_TITLE          = "MSG_DELETE_TX_TITLE";
    public static final String MSG_DELETE_CTCAP             = "MSG_DELETE_CTCAP";
    public static final String MSG_DELETE_CTCAP_TITLE       = "MSG_DELETE_CTCAP_TITLE";
    public static final String MSG_DELETE_FILTERCAP         = "MSG_DELETE_FILTERCAP";
    public static final String MSG_DELETE_FILTERCAP_TITLE   = "MSG_DELETE_FILTERCAP_TITLE";

    public static final String MSG_DELETE_CTCAPPROP         = "MSG_DELETE_CTCAPPROP";
    public static final String MSG_DELETE_CTCAPPROP_TITLE   = "MSG_DELETE_CTCAPPROP_TITLE";

    public static final String MSG_DELETE_CTCAPPROPVAL      = "MSG_DELETE_CTCAPPROPVAL";
    public static final String MSG_DELETE_CTCAPPROPVAL_TITLE= "MSG_DELETE_CTCAPPROPVAL_TITLE";

    public static final String MSG_DELETE_CTCAPPARAM         = "MSG_DELETE_CTCAPPARAM";
    public static final String MSG_DELETE_CTCAPPARAM_TITLE   = "MSG_DELETE_CTCAPPARAM_TITLE";

    public static final String MSG_DELETE_CTCAPPARAMVAL      = "MSG_DELETE_CTCAPPARAMVAL";
    public static final String MSG_DELETE_CTCAPPARAMVAL_TITLE= "MSG_DELETE_CTCAPPARAMVAL_TITLE";

    public static final String MSG_DELETE_FILTERCAPKEY       = "MSG_DELETE_FILTERCAPKEY";
    public static final String MSG_DELETE_FILTERCAPKEY_TITLE = "MSG_DELETE_FILTERCAPKEY_TITLE";

    public static final String MSG_DELETE_FILTERCAPPROP      = "MSG_DELETE_FILTERCAPPROP";
    public static final String MSG_DELETE_FILTERCAPPROP_TITLE= "MSG_DELETE_FILTERCAPPROP_TITLE";

    public static final String MSG_DELETE_EXT_KEY            = "MSG_DELETE_EXT_KEY";
    public static final String MSG_DELETE_EXT_KEY_TITLE      = "MSG_DELETE_EXT_KEY_TITLE";

    public static final String MSG_DISCARD_CHANGES           = "MSG_DISCARD_CHANGES";
    public static final String MSG_DISCARD_CHANGES_TITLE     = "MSG_DISCARD_CHANGES_TITLE";
    
    public static final String MSG_SAVE_CHANGES              = "MSG_SAVE_CHANGES";
    public static final String MSG_SAVE_CHANGES_TITLE        = "MSG_SAVE_CHANGES_TITLE";

    //new objects names
    public static final String NEW_PROP                     = "NEW_PROP";
    public static final String NEW_CTCAP_PROP_PARAM         = "NEW_CTCAP_PROP_PARAM";
    public static final String NEW_VALUE                    = "NEW_VALUE";
    public static final String NEW_TYPE                     = "NEW_TYPE";
    public static final String NEW_VERSION                  = "NEW_VERSION";
    public static final String NEW_DATASTORE                = "NEW_DATASTORE";
    public static final String NEW_EXT                      = "NEW_EXT";
    public static final String NEW_KEYWORD                  = "NEW_KEYWORD";

    public static final String ERROR_CREATING               = "ERROR_CREATING";

    // ------------------------------------------------------------ Private data
    private static final ResourceBundle rb = NbBundle.getBundle(Bundle.class);

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
