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


import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.mail.Message;
import javax.mail.MessagingException;
import com.funambol.email.exception.EntityException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import javax.mail.internet.MailDateFormat;

/**
 * <p>Utility class for Date fields</p>
 *
 * @version $Id: UtilityDate.java,v 1.1 2008-03-25 11:25:34 gbmiglia Exp $
 */

public class UtilityDate {

    //------------------------------------------------------------------ CONSTANTS

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);


    // ------------------------------------------------------------ Public Methods


    /**
     * Returns an origination date in UTC format
     * <p>
     * Available Date Parser
     * <p>
     * pat_1: Exchange 2003 (eng): wed, 6 Oct 2005 09:46:47 +0200
     * <p>
     * pat_2: Exchange 2000 (eng): 6 Oct 2005 09:46:47 +0200
     * <p>
     * pat_3: BB client          : Wed Dec 28 20:05:53 GMT 2005
     * <p>
     * pat_4: ??                 : Wed Dec 28 20:05:53 2005
     *
     * @param message Message
     * @param loc     Locale
     * @return date in the header Date
     */
    public static Date getHeaderDate(Message message, Locale loc)
    throws EntityException {

        Date origDate = null;
        String[] values = null;

        String subj = "";
        try {
            subj = message.getSubject();
        } catch (MessagingException me2){
            // do nothing
        }

        try {
            values = message.getHeader(Def.HEADER_DATE);
        } catch (MessagingException me) {
            throw new EntityException(
                    "Error getting Header Date for email: [" + subj + "]", me);
        }

        if ((values != null) && (values.length > 0)) {
            origDate = dateStringToDate(values[0], subj);
        }

        return origDate;
    }

    /**
     *
     * @param message Message
     * @return String
     */
    public static Date getHeaderReceived(Message message, Locale loc) {

        try {
            Date d = null;

            d = getHeaderReceivedWithParser(message,loc);
            if (d == null){
                d = message.getReceivedDate();
            }

            return d;
        } catch (Exception e) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * get the received date in the header with a manual parser
     * the received date could be in the following farmats:
     * <br>
     * Received: from 64.142.123.30  (EHLO a.mail.sonomait.com) (64.142.123.30)
     *   by mta108.mail.re3.yahoo.com with SMTP; Wed, 07 Feb 2007 08:13:41 -0800
     * <br>
     * <br>
     * Received: from localhost (localhost.localdomain [127.0.0.1])
     * by a.mail.sonomait.com (Postfix) with ESMTP id E12452195455
     * for <gbmiglia@yahoo.it>; Wed,  7 Feb 2007 08:13:38 -0800 (PST)
     * <br>
     *
     *
     * @param message Message
     * @return String
     */
    private static Date getHeaderReceivedWithParser(Message message, Locale loc) {

        try {
            String[] recs  = message.getHeader(Def.HEADER_RECEIVED);
            String recValue = null;
            Date d = null;
            // get the latest received date
            if (recs != null){
                recValue = recs[0]; // recs[recs.length-1];
                if (recValue != null){
                    // get the date value
                    int semicolonIndex = recValue.indexOf(";");
                    if (semicolonIndex != -1){
                        recValue = recValue.substring(semicolonIndex+1,recValue.length());
                        d = formatter(recValue.trim(), loc);
                    }
                }
            }
            return d;
        } catch (Exception e) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * Returns true if the date is parsable
     *
     * @param message Message
     * @return true if the header date is parsable
     */
    public static boolean isHeaderDateParsable(Message message) {

        boolean check = true;

        try {
            String[] values = message.getHeader(Def.HEADER_DATE);
            if ((values != null) && (values.length > 0)) {
                String s = values[0];
                MailDateFormat mailDateFormat = new MailDateFormat();
                Date origDate = mailDateFormat.parse(s);
                if (origDate == null) {
                    check = false;
                }
            } else {
                // there is not the Date
                check = false;
            }
        } catch (Exception me) {
            check = false;
            // do nothing
        }

        return check;
    }



    /**
     *
     * @param message Message
     * @return String
     */
    public static Date getDateForTimeFilter(Message message, Locale loc) {

        Date d = null;
        try {
            d = getHeaderReceived(message, loc);
            if (d == null){
                // this method returns always a date
                d = getHeaderDate(message,loc);
            }
        } catch (Exception e) {
            // do nothing; we just want to return null
        }
        return d;
    }


    /**
     *
     * this is a fix for all strange date format;
     * it returns the NOW value.
     *
     */
    public static String  getNowPatch(){
        java.util.Date d = new java.util.Date(System.currentTimeMillis());
        DateFormat utcf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String out = utcf.format(d);
        return out;

    }

    /**
     * this method always returns a Date value.
     * if the parser returns a null value, it applies the getHeaderDatePatch3().
     * the getHeaderDatePatch3() method returns the NOW value.
     *
     * set public for testing purpose
     *
     */
    private static Date dateStringToDate(String s, String subj)
    throws EntityException {

        Date origDate = null;

        try {
            MailDateFormat mailDateFormat = new MailDateFormat();
            origDate = mailDateFormat.parse(s);
        } catch (Exception e1) {
            throw new EntityException(
                    "Error parsing Header Date for email: [" + subj + "]", e1);
        }

        try {
            if (origDate == null) {
                s = getNowPatch();
                origDate = formatter(s, null);
            }
        } catch (Exception e1) {
            throw new EntityException(
                    "Error parsing Header Date for email: [" + subj + "]", e1);
        }


        return origDate;
    }


    /**
     *
     */
    private static Date formatter(String s, Locale loc)
    throws EntityException {

        if (loc == null) {
            loc = Locale.US;
        } else {
            loc = Locale.US;
        }

        DateFormat pat_1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", loc);
        DateFormat pat_2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z"     , loc);
        DateFormat pat_3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy" , loc);
        DateFormat pat_4 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy"   , loc);

        Date origDate = null;
        try {
            origDate = pat_1.parse(s);
        } catch (Exception e1) {
            try {
                origDate = pat_2.parse(s);
            } catch (Exception e2) {
                try {
                    origDate = pat_3.parse(s);
                } catch (Exception e3) {
                    try {
                        origDate = pat_4.parse(s);
                    } catch (Exception e4) {
                        throw new EntityException("Error parsing Header Date", e4);
                    }
                }
            }
        }
        return origDate;
    }

}
