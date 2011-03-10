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

package com.funambol.framework.server.error;

import com.funambol.framework.core.StatusCode;
import com.funambol.framework.core.Sync4jException;

/**
 * @version $Id: ServerException.java,v 1.2 2007-11-28 11:15:59 nichele Exp $
 *
 */
public class ServerException extends Sync4jException {

    // --------------------------------------------------------------- Constants

    public static int UNKNOWN = -1;

    // ------------------------------------------------------------ Private data

    private int statusCode = UNKNOWN;

    // ------------------------------------------------------------ Constructors

    /**
     * @param strMsg  human readable String that describes the cause of the
     *      exception
     */
    public ServerException(final String strMsg) {
        super(strMsg);
    }

    /**
     * Constructor for the ServerException object
     *
     * @param statusCode  Description of the Parameter
     * @param strMsg      Description of the Parameter
     */
    public ServerException(final int statusCode, final String strMsg) {
        this(statusCode, strMsg, null);
    }

    public ServerException(final String strMsg, final Throwable cause) {
        this(UNKNOWN, strMsg, cause);
    }

    public ServerException(final int statusCode, final String strMsg, final Throwable cause) {
        super(strMsg, cause);

        this.statusCode = statusCode;
    }

    public ServerException(final Throwable cause) {
        this(UNKNOWN, "", cause);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the statusCode attribute of the ServerException object
     *
     * @return   The statusCode value
     */
    public int getStatusCode() {
        return statusCode;
    }


    /**
     * Returns the SyncML message associated to the status code of this exception
     *
     * @return the SyncML message associated to the status code of this exception
     */
    public String getSyncMLMessage() {
        int code = getStatusCode();

        String msg = StatusCode.getStatusDescription(code);

        return (msg == null) ? "" : msg;
    }

    /**
     * Redefines the standard <i>getMessage()</i> method.
     *
     * @return <status code> <status description> - <message description>
     */
    public String getMessage() {
        String message;
        if (statusCode != UNKNOWN) {
            message =  statusCode
                       + " "
                       + getSyncMLMessage()
                       + " - "
                       + super.getMessage()
                       ;
        } else {
            message = super.getMessage();
        }
        return message;
    }

}
