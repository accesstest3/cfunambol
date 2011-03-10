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

package com.funambol.email.items.manager;

import com.funambol.common.pim.converter.ConverterException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * This class contains static public wrapper methods around private methods of
 * class <code>MessageCreator</code>.
 * @author $Id: MessageCreatorMethodWrappers.java,v 1.1 2008-03-25 11:25:33 gbmiglia Exp $
 */
public class MessageCreatorMethodWrappers {

    /**
     * Wrapper around <code>encodeHeaderField</code> private method.
     */
    public static String encodeHeaderField(String name, String value){

        String newValue = null;
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("encodeHeaderField")) {
                    m.setAccessible(true);
                    newValue = (String) m.invoke(null, name, value);
                    break;
                }
            }

        } catch (Exception e) {
        }
        return newValue;
    }

    /**
     * Wrapper around <code>getFieldLength</code> private method.
     */
    public static int getFieldLength(String name, String value){
        
        int length = -1;
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("getFieldLength")) {
                    m.setAccessible(true);
                    length = ((Integer) m.invoke(null, name, value)).intValue();
                    break;
                }
            }

        } catch (Exception e) {
        }
        return length;
        
    }

    /**
     * Wrapper around <code>getFieldLength</code> private method.
     */
    public static boolean isHeaderMinimum(String name) {
        
        boolean isMin = false;
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("isHeaderMinimum")) {
                    m.setAccessible(true);
                    isMin = ((Boolean) m.invoke(null, name)).booleanValue();
                    break;
                }
            }

        } catch (Exception e) {
        }
        return isMin;
        
    }

    public static String vCal2HumanReadableCalendar (String partContent) {    
        
        //
        // Extract the vcaledar section from the content.
        //
        int i = partContent.indexOf("BEGIN:VCALENDAR");
        String vcalText = partContent.substring(i).trim();

        String humanReadableCalendar = null;
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("vCal2HumanReadableCalendar")) {
                    m.setAccessible(true);
                    humanReadableCalendar = (String) m.invoke(null, vcalText);
                    break;
                }
            }

        } catch (Exception e) {
        }
        
        // replace vcalendar string with the human readable format
        StringBuilder tmp = new StringBuilder(
                partContent.substring(0, i));
        tmp.append(humanReadableCalendar);
        
        return tmp.toString();
    }
    
    public static Object createNewHeader(Message message, Header header) 
            throws MessagingException, UnsupportedEncodingException {
        
        Object newHeaderValue = null;
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("createNewHeader")) {
                    m.setAccessible(true);
                    newHeaderValue = m.invoke(null, message, header);
                    break;
                }
            }

        } catch (Exception e) {
        }
        
        return newHeaderValue;
    }
}
