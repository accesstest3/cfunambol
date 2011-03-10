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

package com.funambol.email.transport;

import com.funambol.email.util.Def;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.AuthenticationFailedException;
import javax.net.ssl.SSLException;


/**
 * This class hanldes the errors given by a mail server while accessing it.
 *
 * @version $Id: MailServerErrorHandler.java,v 1.1 2008-04-03 11:14:42 gbmiglia Exp $
 */
public class MailServerErrorHandler {
    
    // ------------------------------------------------------------ Private Data
    //
    // patterns to match mail server errors
    //
    private static final Pattern loginFailedPattern =
            Pattern.compile(Def.ERR_REGEXP_LOGIN_FAILED);

    private static final Pattern sslError =
            Pattern.compile(Def.ERR_REGEXP_SSL_ERROR);

    private static final Pattern timeOut =
            Pattern.compile(Def.ERR_REGEXP_TIME_OUT);

    private static final Pattern popNotEnabled =
            Pattern.compile(Def.ERR_REGEXP_POP_NOT_ENABLED);

    private static final Pattern domainInvalidPattern =
            Pattern.compile(Def.ERR_REGEXP_DOMAIN_INVALID);

    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Creates a <code>MailServerError</code> object from the given exception.
     *
     * @param e exception which wraps the error from the mail server
     * @return a <code>MailServerError</code> object that represent mail server
     * error.
     */
    public static MailServerError exceptionHandler(Throwable t) {

        Throwable cause = t.getCause();

        if (cause != null) {

            return exceptionHandler(cause);

        } else {

            String errmsg = t.getMessage();
            MailServerError mailServerError =
                    new MailServerError(Def.ERR_GENERIC_MAILSERVER, errmsg);

            if (t instanceof AuthenticationFailedException) {

                //
                // An AuthenticationFailedException is thrown when:
                // - username/password incorrect (LOGIN failed)
                // - domain incorrect (no domain specified or invalid domain specified)
                //
                if (match(errmsg, domainInvalidPattern)) {

                    // username/password incorrect

                    mailServerError = new MailServerError(
                            Def.ERR_INVALID_CREDENTIALS, errmsg);

                } else if (match(errmsg, loginFailedPattern)) {

                    //
                    // domain incorrect message
                    // (no domain specified or invalid domain specified)
                    //
                    mailServerError = new MailServerError(
                            Def.ERR_INVALID_DOMAIN, errmsg);

                } else if (match(errmsg, popNotEnabled)) {

                    // pop not enabled

                    mailServerError = new MailServerError(
                            Def.ERR_POP_NOT_ENABLED, errmsg);
                }

            } else if (t instanceof UnknownHostException) {

                mailServerError = new MailServerError(
                        Def.ERR_UKNOWN_HOST, errmsg);

            } else if (
                    t instanceof SocketTimeoutException ||
                    match(errmsg, timeOut)) {

                //
                // A SocketTimeoutException is thrown when:
                // - store timeout exceeded (Read timed out)
                //

                mailServerError = new MailServerError(
                        Def.ERR_STORE_TIMEOUT, errmsg);

            } else if (
                    t instanceof SSLException ||
                    match(errmsg, sslError)) {

                //
                // SSL error
                //
                mailServerError = new MailServerError(Def.ERR_SSL, errmsg);
            }

            return mailServerError;
        }

    }
    
    //---------------------------------------------------------- Private Methods
    
    /**
     * Matches a string against a pattern.
     *
     * @param pattern
     * @param stringToMatch
     * @return <code>true</code> if string matches patter, <code>false</code>
     * otherwise or the given string is null
     */
    private static boolean match(String stringToMatch, Pattern pattern){

        if (stringToMatch == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(stringToMatch);
        return matcher.matches();
    }    

}
