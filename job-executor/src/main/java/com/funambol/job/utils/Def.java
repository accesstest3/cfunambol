/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
package com.funambol.job.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @version $Id: Def.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class Def {
    public static final String LOG_NAME="job-executor";
    // ----------------------------------------------------- Generic error codes
    public static final String GENERIC_ERROR_CODE    = "JOB-1000";
    public static final String GENERIC_ERROR_MESSAGE = "Generic Job Executor error";

    public static final String NOT_CONFIGURED_TASK_CODE    = "JOB-1001";
    public static final String NOT_CONFIGURED_TASK_MESSAGE = "Task to be executed is not configured";
    
    public static final String NOT_INSTANTIATABLE_TASK_CODE    = "JOB-1002";
    public static final String NOT_INSTANTIATABLE_TASK_MESSAGE = "Task to be executed cannot be instantiated";

    // ------------------------------------------------------------ Private data
    private static final Map<String, String> errorCodes = new HashMap<String, String>();

    static {
        //
        // We want to build a map with all pairs code - message. In order to do
        // that we use reflection.
        //
        Field[] fields = Def.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName != null && fieldName.endsWith("_CODE")) {

                int index = fieldName.lastIndexOf("_CODE");

                String prefix = fieldName.substring(0, index);

                Field messageField = null;

                String codeFieldValue    = null;
                String messageFieldValue = null;

                try {
                    codeFieldValue = (String)field.get(null);
                } catch (IllegalAccessException e) {
                    //
                    // nothing to do...ignoring this exception
                    //
                    continue;
                }

                try {
                    messageField = Def.class.getField(prefix + "_MESSAGE");
                    messageFieldValue = (String)messageField.get(null);
                } catch (NoSuchFieldException e) {
                    //
                    // Unable to retrieve the message...the error will not put
                    // in the map
                    //
                    continue;
                } catch (IllegalAccessException e) {
                    //
                    // Unable to retrieve the message...the error will not put
                    // in the map
                    //
                    continue;
                }

                //
                // We put null value at the moment. The next for cycle will
                // fill the map with the messages
                //
                errorCodes.put(codeFieldValue, messageFieldValue);
            }
        }
    }

    // ---------------------------------------------------------- Public methods

    public static String getErrorMessage(String errorCode) {
        return errorCodes.get(errorCode);
    }

}
