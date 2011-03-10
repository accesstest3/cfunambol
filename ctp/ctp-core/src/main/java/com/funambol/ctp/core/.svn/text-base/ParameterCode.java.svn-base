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
package com.funambol.ctp.core;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * This class represents the possible parameters code.
 *
 * @version $Id: ParameterCode.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class ParameterCode {

    // --------------------------------------------------------------- Constants
    public static final byte DEVID_CODE       = 0x01;
    public static final byte USERNAME_CODE    = 0x02;
    public static final byte CRED_CODE        = 0x03;
    public static final byte FROM_CODE        = 0x04;
    public static final byte TO_CODE          = 0x05;
    public static final byte NONCE_CODE       = 0x06;
    public static final byte SAN_CODE         = 0x07;
    public static final byte DESCRIPTION_CODE = 0x08;

    // ------------------------------------------------------------ Private data
    private static BidiMap codeToName = null;

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of ParameterCode */
    private ParameterCode() {
    }

    static {
        codeToName = new DualHashBidiMap();

        codeToName.put(DEVID_CODE      , "DEVID"      );
        codeToName.put(USERNAME_CODE   , "USERNAME"   );
        codeToName.put(CRED_CODE       , "CRED"       );
        codeToName.put(FROM_CODE       , "FROM"       );
        codeToName.put(TO_CODE         , "TO"         );
        codeToName.put(NONCE_CODE      , "NONCE"      );
        codeToName.put(SAN_CODE        , "SAN"        );
        codeToName.put(DESCRIPTION_CODE, "DESCRIPTION");
    }

    /**
     * Returns the code of the parameter.
     *
     * @param name the parameter's name
     * @return the parameter's code
     */
    public static int getCodeFromName(String name) {
        return Integer.parseInt("" + codeToName.getKey(name));
    }

    /**
     * Returns the parameter's name.
     *
     * @param the parameter's code
     * @return the parameter's name
     */
    public static String getNameFromCode(byte code) {
        return (String)codeToName.get(code);
    }
}
