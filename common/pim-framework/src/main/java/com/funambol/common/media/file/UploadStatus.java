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

package com.funambol.common.media.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class define all the values allowed as upload status for a FileDataObjectMetadata
 * bean. They are:
 * Constant Name        Character Value     Meaning
 * - NOT_STARTED            N               It's the default value, it should mean metadata have been synced but
 *                                          the upload of the media body hasn't started yet.
 * - PARTIALLY_UPLOADED     P               The uploaded of the media body has started but is't not yet completed,
 *                                          it can be resumed.
 * - UPLOADED               U               The upload of the media body has been successfully completed.
 *
 * - EXPORTED               E               The upload of the media body has been successfully completed and
 *
 * We decided not to use , since upload_status attribute needs to be retrieved/persisted
 * from/to the database so it makes no sense to use Enum due to needed conversions.
 *
 * @version $Id$
 */
public class UploadStatus {

    // upload status available values
    public final static char NOT_STARTED        ='N';
    public final static char PARTIALLY_UPLOADED ='P';
    public final static char UPLOADED           ='U';
    public final static char EXPORTED           ='E';

    // upload status description messages
    public final static String NOT_STARTED_DESC
              = "Media upload not started yet";
    public final static String PARTIALLY_UPLOADED_DESC
              = "Media upload partially completed";
    public final static String UPLOADED_DESC
              = "Media upload successfully completed";
    public final static String EXPORTED_DESC
              = "Media uploaded and exported to an third party storage service";

    /**
     * It's a static, unmodifiable map used to store binding between upload
     * status values and description messages.
     */
    private final static Map<Character,String> states;
    static {
        Map<Character,String> temp = new HashMap<Character, String>();
        temp.put(NOT_STARTED,NOT_STARTED_DESC);
        temp.put(PARTIALLY_UPLOADED,PARTIALLY_UPLOADED_DESC);
        temp.put(UPLOADED,UPLOADED_DESC);
        temp.put(EXPORTED,EXPORTED_DESC);
        states = Collections.unmodifiableMap(temp);
    }


    /**
     * This method allows to parse an upload_status value from an input string
     * The input string must be not null, one sized an may contain one of the 
     * following characters ('N','P','U', 'E'). Conversion to uppercase is automatically
     * performed by this method.
     * @param source the input string containing the value that need to be converted
     * to a valid upload_status
     * @throws IllegalArgumentException if the input string cannot be mapped to
     * aany of the allowed values
     * @return the char representing the desired upload_status
     */
    public static char fromString(String source) {
        if(source!=null && source.length()==1) {
           Character candidate = Character.toUpperCase(source.charAt(0));
           if(states.containsKey(candidate)) {
               return candidate;
           }
        }
        throw new IllegalArgumentException("Unable to parse upload status from character '"+source+"'.");
    }
    
    /**
     * @param source it's the upload_status we want to find a description for
     * @return a string containing the description bound to the input upload_status
     * @throws IllegalArgumentException if the input character isn't an allowed
     * value for the upload_status attribute.
     */
    public static String toDescription(char source) {
        Character candidate = Character.toUpperCase(source);
        if(states.containsKey(candidate)) {
            return states.get(candidate);
        }
        throw new IllegalArgumentException("Unable to find description for status '"+source+"'.");
    }

}
