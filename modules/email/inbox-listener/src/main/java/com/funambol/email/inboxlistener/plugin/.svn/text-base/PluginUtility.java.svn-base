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
package com.funambol.email.inboxlistener.plugin;


import com.funambol.email.util.Def;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * <p>Utility class</p>
 *
 * @version $Id: PluginUtility.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */

public class PluginUtility {

    // ------------------------------------------------------------ Public Methods

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Public Methods

    /**
     * Checks if the message is for me.
     *
     * @param input
     * @param total
     * @param index
     * @return
     */
    public static boolean isMyRequest(String input, int total, int index) {

        boolean check = false;

        if (input == null){
            return check;
        }

        input = input.trim();

        int hashNumber = Math.abs(input.hashCode());
        int value = hashNumber % total;
        if (value == index){
            check = true;
        } else {
            check = false;
        }

        String logMsg = "Message [" + input + "] has hashNumber: " + hashNumber +
                        "; Target Server Index: " + value +
                        "; my Server Index is: " + index + ". ";
        if (check){
            if (log.isTraceEnabled()) {
                log.trace( logMsg + "The message will be processed" );
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace( logMsg + "The message will be ignored" );
            }
         }

        return check;
    }

}
