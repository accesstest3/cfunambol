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
package com.funambol.framework.core;

import java.io.PrintStream;
import java.io.PrintWriter;

import java.text.MessageFormat;


/**
 * Because this class must be JDK 1.1.8 compliant, we can't use the <i>cause</i>
 * stuff of the JDK 1.2...
 *
 *
 *
 * @version $Id: Sync4jException.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Sync4jException extends Exception {
   Throwable cause;

    /**
     *
     */
    public Sync4jException() {
    }

   /**
     * @param strMsg  human readable String that describes the cause of the
     *      exception
     */
    public Sync4jException(final String strMsg) {
        super(strMsg);
    }

    /**
     * Constructor for the ServerException object
     *
     * @param strMsg    Description of the Parameter
     * @param aobjArgs  Description of the Parameter
     */
    public Sync4jException(final String strMsg, final Object[] aobjArgs) {
         super(MessageFormat.format(strMsg, aobjArgs));
    }

    public Sync4jException(final String strMsg, final Throwable cause) {
        super(strMsg);

        this.cause = cause;
    }

    public Sync4jException(final Throwable cause) {
        this("", cause);
    }

    public Throwable getCause() {
        return cause;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.err.println("Caused by:");
            cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (cause != null) {
            System.err.println("Caused by:");
            cause.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (cause != null) {
            System.err.println("Caused by:");
            cause.printStackTrace(pw);
        }
    }
}
