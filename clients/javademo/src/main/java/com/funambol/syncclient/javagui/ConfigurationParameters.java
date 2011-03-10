/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003-2007 Funambol, Inc.
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

package com.funambol.syncclient.javagui;

import java.awt.Font;

/**
 * Configuration parameters.
 *
 *
 * @version $Id: ConfigurationParameters.java,v 1.3 2007-12-22 16:41:48 nichele Exp $
 */
public interface ConfigurationParameters {

    //---------------------------------------------------------- Constants

    public final static String KEY_CONFIG              = "Config"            ;
    public final static String KEY_CALENDARLIST        = "CalendarList"      ;
    public final static String KEY_CALENDARMODIFY      = "CalendarModify"    ;
    public final static String KEY_CALENDARNEW         = "NewContact"        ;
    public final static String KEY_CONTACTLIST         = "ContactList"       ;
    public final static String KEY_CONTACTMODIFY       = "ContactModify"     ;
    public final static String KEY_CONTACTNEW          = "NewCalendar"       ;
    public final static String KEY_SYNC                = "Sync"              ;

    public final static String PARAM_ADJUSTDATE        = "adjust-date"       ;
    public final static String PARAM_DEVICEID          = "device-id"         ;
    public final static String PARAM_LOGLEVEL          = "log-level"         ;
    public final static String PARAM_MESSAGETYPE       = "message-type"      ;
    public final static String PARAM_PASSWORD          = "password"          ;
    public final static String PARAM_SOURCEURI         = "sourceURI"         ;
    public final static String PARAM_SOURCEURICALENDAR = "sourceCalendarURI" ;
    public final static String PARAM_SOURCEURICONTACT  = "sourceContactURI"  ;
    public final static String PARAM_SYNCCALENDAR      = "sync-calendar"     ;
    public final static String PARAM_SYNCCONTACT       = "sync-contact"      ;
    public final static String PARAM_SYNCMLURL         = "syncml-url"        ;
    public final static String PARAM_SYNCMODE          = "sync"              ;
    public final static String PARAM_SYNCSOURCEURI     = "sourceURI"         ;
    public final static String PARAM_TARGETLOCALURI    = "target-local-uri"  ;
    public final static String PARAM_USERNAME          = "username"          ;

    public final static String MESSAGE_XML
        = "application/vnd.syncml+xml"                                       ;
    public final static String MESSAGE_WBXML
        = "application/vnd.syncml+wbxml"                                     ;

    public final static String LOG_INFO                = "info"              ;
    public final static String LOG_DEBUG               = "debug"             ;
    public final static String LOG_NONE                = "none"              ;
    
    public final static String DEFAULT_DEVICE_ID       = "fjg-pim-demo"      ;
    public final static String PREFIX_DEVICE_ID        = "fjg-"              ;

    public static final String FRAME_ICONNAME = "images/icon_java_32x32.png"  ;
    public static final String FRAME_LOGONAME = "images/icon_java_128x128.png";

    public final static Font FONT = new Font("Microsoft Sans Serif", 0, 11);
    public final static Font FONT9 = new Font("Microsoft Sans Serif", 0, 9);
    public final static Font FONT_BOLD =
        new Font("Microsoft Sans Serif", Font.BOLD, 11);

}
