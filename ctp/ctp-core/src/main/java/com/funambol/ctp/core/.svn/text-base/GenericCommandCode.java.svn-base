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
 * This class represents the possible command code (both command and status).
 *
 * @version $Id: GenericCommandCode.java,v 1.3 2007-11-28 11:26:14 nichele Exp $
 */
public class GenericCommandCode {

    // --------------------------------------------------------------- Constants
    // Command code
    public static final byte AUTH_CODE  = 0x01;
    public static final byte READY_CODE = 0x02;
    public static final byte BYE_CODE   = 0x03;

    // Status code
    public static final byte OK_CODE                = 0x20;
    public static final byte JUMP_CODE              = 0x37;
    public static final byte ERROR_CODE             = 0x50;
    public static final byte NOT_AUTHENTICATED_CODE = 0x41;
    public static final byte UNAUTHORIZED_CODE      = 0x42;
    public static final byte FORBIDDEN_CODE         = 0x43;
    public static final byte SYNC_CODE              = 0x29;

    // ------------------------------------------------------------ Private data
    private static BidiMap codeToClass = null;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of <code>GenericCommandCode</code>.
     */
    private GenericCommandCode() {
    }

    static {
        codeToClass = new DualHashBidiMap();

        codeToClass.put(AUTH_CODE        , com.funambol.ctp.core.Auth.class );
        codeToClass.put(READY_CODE       , com.funambol.ctp.core.Ready.class);
        codeToClass.put(BYE_CODE         , com.funambol.ctp.core.Bye.class  );
        codeToClass.put(OK_CODE          , com.funambol.ctp.core.Ok.class   );
        codeToClass.put(JUMP_CODE        , com.funambol.ctp.core.Jump.class );
        codeToClass.put(ERROR_CODE       , com.funambol.ctp.core.Error.class);
        codeToClass.put(UNAUTHORIZED_CODE,
                        com.funambol.ctp.core.Unauthorized.class);
        codeToClass.put(NOT_AUTHENTICATED_CODE,
                        com.funambol.ctp.core.NotAuthenticated.class);
        codeToClass.put(FORBIDDEN_CODE   ,
                        com.funambol.ctp.core.Forbidden.class   );
        codeToClass.put(SYNC_CODE        , com.funambol.ctp.core.Sync.class );

    }

    // ---------------------------------------------------------- Public methods
    /**
     * Gets the Class instance from the byte code.
     *
     * @param code the code in byte of the class instance
     * @return the Class
     */
    public static Class getClassFromCode(byte code) {
        return (Class)codeToClass.get(code);
    }

    /**
     * Gets the command code from the Class.
     *
     * @param classname the Class to find code
     * @return the command code
     */
    public static int getCodeFromClass(Class classname) {
        return Integer.parseInt("" + codeToClass.getKey(classname));
    }
}
