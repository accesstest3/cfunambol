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

package com.funambol.common.pim.vcard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.TimeZone;

import com.funambol.common.pim.contact.Contact;

/**
 * This class parses an input file formatted according to
 * versit vCard 2.1 specification and generates an object model of the contact
 * using the package com.funambol.pdi.contact
 *
 * @see Contact
 * @version $Id: VCardParser.jj,v 1.7 2008-08-26 15:51:24 luigiafassina Exp $
 */
public class VcardParser {

    private static final String DEFAULT_CHARSET =
        new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();

    // the default timezone to use if datetimes are not UTC
    // if null, no conversion is performed
    //
    private TimeZone defaultTimeZone = null;

    //
    // the default charset to use if some properties are encoded
    // but without the charset
    //
    private String defaultCharset = DEFAULT_CHARSET;

    private InputStream is = null;

    public VcardParser(InputStream is, String tz, String defaultCharset) {
        this(is);

        if (tz != null) {
            defaultTimeZone = TimeZone.getTimeZone(tz);
        }
        if (defaultCharset != null) {
            this.defaultCharset = defaultCharset;
        }
    }

    public VcardParser(InputStream is) {
        this.is = is;
    }


    public static void main(String args[]) {
        StringBuffer vCard=new StringBuffer();
        String line;
        try {
            BufferedReader reader=new BufferedReader(
                                      new InputStreamReader(System.in));
            line=reader.readLine();
            while (line!=null){
                vCard.append(line+"\r\n");
                line=reader.readLine();
            }
            System.out.println(vCard);
            ByteArrayInputStream buffer;
            VcardParser parser;
            buffer = new ByteArrayInputStream(vCard.toString().getBytes());
            parser = new VcardParser(buffer, null, "UTF-8");
            parser.vCard();
            System.out.println("vcard file parsed successfully.");
        } catch (ParseException e) {
            System.out.println("vcard file failed to parse.");
            System.out.println(e.toString());
        }catch (IOException ioe){
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }
    // This equal to the grammar parsing rule, but javacc is not able
    // to use equal names rules with different arguments as in java.
    public Contact vCard() throws ParseException {
        return vCard(new Contact());
    }

    public Contact vCard(Contact contact) throws ParseException {
        VCardSyntaxParser parser = new VCardSyntaxParser(is);
        VCardSyntaxParserListener lis;
        lis = new VCardSyntaxParserListenerImpl(contact, defaultTimeZone, defaultCharset);
        parser.setListener(lis);
        parser.parse();
        return contact;
    }
}

 
