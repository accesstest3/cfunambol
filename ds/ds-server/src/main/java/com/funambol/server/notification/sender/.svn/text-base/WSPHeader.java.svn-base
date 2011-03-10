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
package com.funambol.server.notification.sender;

/**
 * This class represents a WSP header.
 *
 * @version $Id: WSPHeader.java,v 1.1.1.1 2008-02-21 23:35:53 stefano_fornari Exp $
 */
public class WSPHeader {

    // --------------------------------------------------------------- Constants

    /**
     * To obtain CONTENT_TYPE_CODE_NOTIFICATION, add 0x80 to content type value.
     *  For example, application/vnd.syncml.ds.notification has 0x4E as value,
     *  so 0x80 + 0x4E = 0xCE
     * Other content type values:
     *  http://www.wapforum.org/wina/wsp-content-type.htm
     *
     * Android only supports this content type:
     *  application/vnd.oma.drm.rights+xml: 0x4a
     *  application/vnd.oma.drm.rights+wbxml: 0x4b
     *  application/vnd.wap.sic: 0x2e
     *  application/vnd.wap.slc: 0x30
     *  application/vnd.wap.coc: 0x32
     *  application/vnd.wap.mms-message: 0x3e
     * Check com.android.internal.telephony.WspTypeDecoder.java class at
     *  http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.1_r2/com/android/internal/telephony/WspTypeDecoder.java
     */
    public static final byte CONTENT_TYPE_CODE_NOTIFICATION = (byte)0xCE;
    public static final byte CONTENT_TYPE_CODE_BOOTSTRAP    = (byte)0xC2;

    // ------------------------------------------------------------ Private data
    private byte[] header = null;

    private WSPHeader() {}

    public WSPHeader(byte contentTypeCode) {

        header = new byte[6];

        // Transaction ID / Push ID
        header[0] = (byte)0x01;

        // PDU Type(push)
        header[1] = (byte)0x06;

        // Headerslength (content type + headers)
        header[2] = (byte)0x03;

        // Content type
        header[3] = contentTypeCode;

        // X-WAP-Application-ID
        header[4] = (byte)0xAF;

        // Id for urn: x-wap-application:push.syncml
        header[5] = (byte)0x85;
    }
    
    public WSPHeader(byte[] contentType) {

        header = new byte[5 + contentType.length];

        // Transaction ID / Push ID
        header[0] = (byte)0x01;

        // PDU Type(push)
        header[1] = (byte)0x06;

        // Headerslength (content type + headers)
        header[2] = (byte)(2 + contentType.length);

        // Content type
        System.arraycopy(contentType, 0, header, 3, contentType.length);

        // X-WAP-Application-ID
        header[3 + contentType.length ] = (byte)0xAF;

        // Id for urn: x-wap-application:push.syncml
        header[4 + contentType.length] = (byte)0x85;
    }    

    // ---------------------------------------------------------- Public Methods

    public byte[] getHeader() {
        return header;
    }
}
