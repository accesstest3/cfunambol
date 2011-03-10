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
package com.funambol.email.util;

import com.funambol.email.exception.EntityException;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * @version $Id: Html2Txt.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class Html2Txt {

    /**
     * the method converts the html body into text
     */
    public static String htmltotext(String html) throws EntityException {

        Pattern pDel_1  = Pattern.compile("(?s)<.*?>");
        Pattern pDel_2  = Pattern.compile("&nbsp;");
        Pattern pDel_3  = Pattern.compile("&egrave;");
        Pattern pDel_4  = Pattern.compile("&agrave;");

        Pattern pText   = Pattern.compile("(.*)");

        Matcher m;

        // all lines of the file
        Vector lines = new Vector();
        lines.clear();

        try {
            BufferedReader br = new BufferedReader(new StringReader(html));
            String temp       = br.readLine();

            while(temp != null) {

                m = pDel_1.matcher(temp);
                temp = m.replaceAll(" ");
                temp = temp.trim();

                m = pDel_2.matcher(temp);
                temp = m.replaceAll(" ");
                temp = temp.trim();

                try {
                    m = pDel_3.matcher(temp);
                    temp = m.replaceAll(getEgraveString());
                    temp = temp.trim();
                } catch (Exception e) {
                    // do nothing
                }
                try {
                    m = pDel_4.matcher(temp);
                    temp = m.replaceAll(getAgraveString());
                    temp = temp.trim();
                } catch (Exception e) {
                    // do nothing
                }

                // @todo : implement the rest of characters

                m = pText.matcher(temp);

                if(m.matches()) {
                    String text = m.group(1);
                    text = text.trim();
                    lines.add(text);
                }

                temp = br.readLine();
            }

        } catch (Exception e){
            throw new EntityException("Error Parsing the HTML body");
        }

        String body = "";
        try {
            body = toString(lines);
        } catch (Exception e){
            throw new EntityException("Error Getting the TXT body from HTML");
        }

        return body;
    }


    //---------------------------------------------------------- private methods

    /**
     *
     */
    private static String toString(Vector lines)
    throws EntityException {
        StringBuffer body = new StringBuffer();
        String temp_Str= null;
        try {
            for(int i = 0; i < lines.size(); i++) {
                temp_Str = (String)lines.get(i);
                body.append(temp_Str + "\n");
            }
        } catch (Exception e){
            throw new EntityException("Error Getting the TXT body");
        }
        return body.toString();
    }

    /**
     *  get italian:  e'
     *  <p>
     *  e' = c3a8 (UTF-8)
     */
    private static String getEgraveString() throws UnsupportedEncodingException {
        byte[] egrave       = {(byte)0xc3, (byte)0xa8};
        String egraveString = new String(egrave, "UTF-8");
        return egraveString;
    }

    /**
     *  get italian:  a'
     *  <p>
     *  a' = c3a0 (UTF-8)
     */
    private static String getAgraveString() throws UnsupportedEncodingException {
        byte[] egrave       = {(byte)0xc3, (byte)0xa0};
        String egraveString = new String(egrave, "UTF-8");
        return egraveString;
    }

}
