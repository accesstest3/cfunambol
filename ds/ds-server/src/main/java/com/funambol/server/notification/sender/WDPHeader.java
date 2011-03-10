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
package com.funambol.server.notification.sender;

/**
 * This class represents a WDP header.
 *
 * @version $Id: WDPHeader.java,v 1.1.1.1 2008-02-21 23:35:53 stefano_fornari Exp $
 */
public class WDPHeader {

    // --------------------------------------------------------------- Constants
    public static final int DEFAULT_WAP_PUSH_DESTINATION_PORT = 2948;

    // ------------------------------------------------------------ Private data
    private byte[] header = null;

    public WDPHeader() {
        this(DEFAULT_WAP_PUSH_DESTINATION_PORT);
    }

    public WDPHeader(int destinationPort) {

        if (destinationPort > 65535) {
            throw new IllegalArgumentException("The destination port must be between 0 and 65535");
        }

        String sHex = Integer.toString(destinationPort, 16);
        int len = sHex.length();
        if (len == 1) {
            sHex = "000" + sHex;
        } else if (len == 2) {
            sHex = "00" + sHex;
        } else if (len == 3) {
            sHex = "0" + sHex;
        }
        byte destinationPortHigh = (byte)Integer.parseInt(sHex.substring(0, 2), 16);
        byte destinationPortLow =  (byte)Integer.parseInt(sHex.substring(2), 16);

        // User-Data-Header(UDHL) Length = 6 bytes
        byte length = 6;

        header = new byte[length + 1];

        header[0] = length;

        // UDH IE Identifier Port Number
        header[1] = (byte)0x05;

        // UDH port number IE length
        header[2] = (byte)0x04;

        // Destination port (high)
        header[3] = destinationPortHigh;

        // Destination port (low)
        header[4] = destinationPortLow;

        // Origination port (high)
        header[5] = (byte)0xC0;

        // Origination port (low)
        header[6] = (byte)0x02;

    }

    public WDPHeader(int destinationPort,
                     int originatorPort) {

        if (destinationPort > 65535) {
            throw new IllegalArgumentException("The destination port must be between 0 and 65535");
        }
        if (originatorPort > 65535) {
            throw new IllegalArgumentException("The destination port must be between 0 and 65535");
        }

        //
        // Handling destination port
        //
        String sHex = Integer.toString(destinationPort, 16);
        int len = sHex.length();
        if (len == 1) {
            sHex = "000" + sHex;
        } else if (len == 2) {
            sHex = "00" + sHex;
        } else if (len == 3) {
            sHex = "0" + sHex;
        }
        byte destinationPortHigh = (byte)Integer.parseInt(sHex.substring(0, 2), 16);
        byte destinationPortLow =  (byte)Integer.parseInt(sHex.substring(2), 16);

        //
        // Handling originator port
        //
        sHex = Integer.toString(originatorPort, 16);
        len = sHex.length();
        if (len == 1) {
            sHex = "000" + sHex;
        } else if (len == 2) {
            sHex = "00" + sHex;
        } else if (len == 3) {
            sHex = "0" + sHex;
        }
        byte originatorPortHigh = (byte)Integer.parseInt(sHex.substring(0, 2), 16);
        byte originatorPortLow =  (byte)Integer.parseInt(sHex.substring(2), 16);


        // User-Data-Header(UDHL) Length = 6 bytes
        byte length = 6;

        header = new byte[length + 1];

        header[0] = length;

        // UDH IE Identifier Port Number
        header[1] = (byte)0x05;

        // UDH port number IE length
        header[2] = (byte)0x04;

        // Destination port (high)
        header[3] = destinationPortHigh;

        // Destination port (low)
        header[4] = destinationPortLow;

        // Origination port (high)
        header[5] = originatorPortHigh;

        // Origination port (low)
        header[6] = originatorPortLow;

    }

    // ---------------------------------------------------------- Public Methods

    public byte[] getHeader() {
        return header;
    }
}
