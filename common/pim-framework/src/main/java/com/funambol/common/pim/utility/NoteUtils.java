/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.common.pim.utility;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.note.Note;

/**
 * This class contains various methods for manipulating <code>Note</code> objects.
 * @author $Id: NoteUtils.java,v 1.2 2008-07-14 13:07:10 piter_may Exp $
 */
public class NoteUtils {
    
    /**
     * Checks if two <code>Note</code> objects are deep equals.
     * <p/>
     * <code>uid</code>s are not compared, since this field is related to the sync
     * stuff.
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean deepEquals(Note a, Note b){

        if (!deepEquals(a.getCategories(), b.getCategories())) {
            return false;
        }
        if (!deepEquals(a.getColor(), b.getColor())) {
            return false;
        }
        if (!deepEquals(a.getFolder(), b.getFolder())) {
            return false;
        }
        if (!deepEquals(a.getHeight(), b.getHeight())) {
            return false;
        }
        if (!deepEquals(a.getLeft(), b.getLeft())) {
            return false;
        }
        if (!deepEquals(a.getTextDescription(), b.getTextDescription())) {
            return false;
        }
        if (!deepEquals(a.getTop(), b.getTop())) {
            return false;
        }
        if (!deepEquals(a.getWidth(), b.getWidth())) {
            return false;
        }
        
        //@TODO compare xTags
        
        return true;
    }
    
    // --------------------------------------------------------- Private methods
    private static boolean deepEquals(Property a, Property b){
        String aString = a.getPropertyValueAsString();
        String bString = b.getPropertyValueAsString();
        
        if (aString == null && bString != null){
            return false;
        }
        if (aString != null && bString == null){
            return false;
        }
        if (aString == null && bString == null){
            return true;
        }
        
        if (!aString.equals(bString)) {
            return false;
        }
        return true;        
    }
}








