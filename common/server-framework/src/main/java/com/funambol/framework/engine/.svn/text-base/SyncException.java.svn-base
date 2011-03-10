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
package com.funambol.framework.engine;

import com.funambol.framework.core.StatusCode; // Mmmhh.... is this a good dependency?

/**
 *
 *
 * @version $Id: SyncException.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncException extends java.lang.Exception {

    // ------------------------------------------------------------ Private data

    private int statusCode;
    // if set false means that the exception must be log at error level,
    // otherwise it should be logged at trace level
    private boolean silent = false;

    // ------------------------------------------------------------ Constructors

    public SyncException(String msg) {
        this(msg, null, StatusCode.PROCESSING_ERROR, false);
    }

    public SyncException(String msg, boolean silent) {
        this(msg, null, StatusCode.PROCESSING_ERROR, silent);
    }

    public SyncException(String msg, int statusCode) {
        this(msg, null, statusCode, false);
    }

    public SyncException(String msg, int statusCode, boolean silent) {
        this(msg, null, statusCode, silent);
    }

    public SyncException(String msg, Throwable cause) {
        this(msg, cause, StatusCode.PROCESSING_ERROR, false);
    }

    public SyncException(String msg, Throwable cause, boolean silent) {
        this(msg, cause, StatusCode.PROCESSING_ERROR, silent);
    }

    public SyncException(String msg, Throwable cause, int statusCode) {
        this(msg, cause, statusCode, false);
    }

    public SyncException(Throwable cause) {
        this(cause.getMessage(), cause, StatusCode.PROCESSING_ERROR, false);
    }

    public SyncException(Throwable cause, boolean silent) {
        this(cause.getMessage(), cause, StatusCode.PROCESSING_ERROR, silent);
    }

    public SyncException(String msg, Throwable cause, int statusCode, boolean silent) {
        super(msg, cause);
        this.statusCode = statusCode;
        this.silent = silent;
    }
    // ---------------------------------------------------------- Public methods

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSilent() {
        return silent;
    }
}
