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

package com.funambol.framework.protocol.v11;

/**
 *
 *
 * @version $Id: Errors.java,v 1.1.1.1 2008-02-21 23:35:40 stefano_fornari Exp $
 */
public interface Errors {
    public static final String ERRMSG_DTD_VER_NOT_SUPPORTED        =
       "DTD ver {0} not supported"
    ;
    public static final String ERRMSG_PROTOCOL_VER_NOT_SUPPORTED   =
       "Protocol ver {0} not supported"
    ;
    public static final String ERRMSG_NO_SESSION_ID                =
       "Missing session ID"
    ;
    public static final String ERRMSG_NO_MESSAGE_ID                =
       "Missing message ID"
    ;
    public static final String ERRMSG_INVALID_TARGET               =
       "Invalid target ({0})"
    ;
    public static final String ERRMSG_INVALID_SOURCE               =
       "Invalid source({0})"
    ;
    public static final String ERRMSG_INVALID_ALERT                =
       "Invalid alert command ({0})"
    ;
    public static final String ERRMSG_INVALID_ALERT_CODE           =
       "The alert code {0} is not an initialization code"
    ;
    public static final String ERRMSG_MISSING_ITEM                 =
       "The item element is missing"
    ;
    public static final String ERRMSG_MISSING_SYNC_ANCHOR          =
       "The sync anchor element is missing"
    ;
    public static final String ERRMSG_INVALID_CAPABILITIES         =
       "Invalid {0} capabilities ({1})"
    ;
    public static final String ERRMSG_INVALID_CAPABILITIES_REQUEST =
       "Invalid capabilities request ({1})"
    ;
    public static final String ERRMSG_BODY_NOT_FINAL               =
       "The body packet is not marked 'final'"
    ;
    public static final String ERRMSG_MISSING_STATUS_COMMAND       =
       "Missing status command for command {1}"
    ;
    public static final String ERRMSG_MISSING_RESULTS_COMMAND      =
       "Missing results command for command {1}"
    ;
    public static final String ERRMSG_MISSING_SYNC_COMMAND         =
       "Missing sync command for command"
    ;
    public static final String ERRMSG_INVALID_SYNC_COMMAND         =
       "Invalid sync command ({1})"
    ;
    public static final String ERRMSG_INVALID_MODIFICATION_COMMAND =
       "Invalid modification command ({1})"
    ;
    public static final String ERRMSG_MISSING_TYPE                 =
       "Missing metadata type in {1}"
    ;
    public static final String ERRMSG_MISSING_DATA                 =
       "Missing data in {1}"
    ;
}