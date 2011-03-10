/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.util;


import com.funambol.email.exception.InboxListenerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>InboxListener Utility class</p>
 *
 * @version $Id: UtilityIL.java,v 1.1 2008-03-25 11:25:34 gbmiglia Exp $
 */

public class UtilityIL {

    //------------------------------------------------------------------ CONSTANTS


    // ------------------------------------------------------------ Public Methods

    /**
     *
     */
    public static int readInteger() throws InboxListenerException {

        InputStreamReader iReader = new InputStreamReader(System.in);
        BufferedReader indata     = new BufferedReader(iReader);

        int    value = -1;
        String temp  = null;
        try {
            temp = indata.readLine();
            value = Integer.parseInt(temp);
        } catch (IOException ex) {
            throw new InboxListenerException("Error reading input value (" +
                    ex.getMessage() + ")");
        } catch (NumberFormatException ex) {
            throw new InboxListenerException("Error converting input value (" +
                    ex.getMessage() + ")");
        }

        return value;
    }

    /**
     *
     */
    public static long readLong() throws InboxListenerException {

        InputStreamReader iReader = new InputStreamReader(System.in);
        BufferedReader indata     = new BufferedReader(iReader);

        long    value = -1;
        String temp  = null;
        try {
            temp = indata.readLine();
            value = Long.parseLong(temp);
        } catch (IOException ex) {
            throw new InboxListenerException("Error reading input value (" +
                    ex.getMessage() + ")");
        } catch (NumberFormatException ex) {
            throw new InboxListenerException("Error converting input value (" +
                    ex.getMessage() + ")");
        }

        return value;
    }

    /**
     *
     */
    public static String readValue() throws InboxListenerException {

        InputStreamReader iReader = new InputStreamReader(System.in);
        BufferedReader indata     = new BufferedReader(iReader);
        String value  = null;
        try {
            value = indata.readLine();
        } catch (IOException ex) {
            throw new InboxListenerException("Error reading input value (" +
                    ex.getMessage() + ")");
        }
        return value;
    }


    /**
     * used in the ListenerRegistry
     * the reloading_time in the XML file is in minute format.
     * but we have to convert in millisec
     */
    public static int setMilliSecondsFromMinutes(int minutes) {
        int msecs = 600000; // default 10 minutes
        msecs = ((minutes * 60) * 1000);
        return msecs;
    }

    /**
     * used in the ListenerRegistryDAO
     * the refresh_time in the DB is in second format.
     * but we have to convert in millisec
     */
    public static int setMilliSecondsFromSeconds(int seconds) {
        int msecs = 600000; // default 10 minutes
        msecs = seconds * 1000;
        return msecs;
    }


}
