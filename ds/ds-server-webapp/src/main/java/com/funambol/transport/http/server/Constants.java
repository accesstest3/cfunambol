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
package com.funambol.transport.http.server;

/**
 *
 * @version $Id: Constants.java,v 1.2 2008-05-22 19:41:11 nichele Exp $
 */
public interface Constants {
    public static final String PARAM_SESSION_ID       = "sid"                 ;

    public static final String PARAM_SYNCHOLDER_CLASS = "sync-holder-class"   ;
    public static final String PARAM_HOLDER_CACHE_FACTORY
                                                      = "holder-cache-factory";
    public static final String PARAM_HOLDER_CACHE_CLASS
                                                      = "holder-cache-class"  ;
    public static final String PARAM_GROUP            = "group"               ;
    public static final String PARAM_CHANNEL_PROPERTIES
                                                      = "channel-properties"  ;
    public static final String PARAM_SESSION_TIMEOUT  = "session-timeout";
    public static final String PARAM_JNDI_ADDRESS     = "jndi-address"        ;

    public static final String DEFAULT_GROUP          =
        "funambol.reference_cache";
    public static final long   DEFAULT_TTL            = 600000; // 10 mins
    public static final long   DEFAULT_TIMEOUT        = 10000 ; // 10 secs
    public static final String DEFAULT_JNDI_ADDRESS   = "localhost:1009"      ;

    public static final String LOG_NAME               = "transport.http"      ;

    public static final String PARAM_LOG_MESSAGES     = "log-messages"        ;
    public static final String PARAM_DIRLOG_MESSAGES  = "dirlog-messages"     ;

    public static final String PATH_INFO_STATUS       = "status"              ;
    public static final String PATH_INFO_INFO         = "info"                ;

    public static final String PARAM_PREFERRED_ENCODING = "preferred-encoding";
    public static final String PARAM_SUPPORTED_ENCODING = "supported-encoding";
    public static final String PARAM_COMPRESSION_LEVEL  = "compression-level" ;
    public static final String PARAM_ENABLE_COMPRESSION = "enable-compression";

    public static final String  DEFAULT_PREFERRED_ENCODING = "gzip";
    public static final String  DEFAULT_SUPPORTED_ENCODING = "gzip,deflate";
    
    /** The default compression level of the Deflater class */
    public static final int     DEFAULT_COMPRESSION_LEVEL  = -1;
    public static final boolean DEFAULT_ENABLE_COMPRESSION = true;

    public static final String SESSION_ATTRIBUTE_SYNC_HOLDER =
        "funambol.sync-holder";
    public static final String SESSION_ATTRIBUTE_LOG_CONTEXT =
        "funambol.log-context";

    //
    // Supported compression types
    //
    public static final String COMPRESSION_TYPE_GZIP    = "gzip"   ;
    public static final String COMPRESSION_TYPE_DEFLATE = "deflate";

    public static final String HEADER_ACCEPT_ENCODING  = "Accept-Encoding" ;
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_SIZE_THRESHOLD   = "Size-Threshold"  ;
    public static final String HEADER_UNCOMPRESSED_CONTENT_LENGTH =
        "Uncompressed-Content-Length";

    public static final String HEADER_X_FUNAMBOL_FORCE_COOKIES = "x-funambol-force-cookies";
    public static final String HEADER_X_FUNAMBOL_DS_SERVER     = "X-funambol-ds-server";
    
    public static final String HEADER_X_FUNAMBOL_CLIENT_REMOTE_ADDRESS = "X-funambol-client-remote-address";

    public static final String ATTRIBUTE_LAST_REQUEST = "funambol.last-request";
}
