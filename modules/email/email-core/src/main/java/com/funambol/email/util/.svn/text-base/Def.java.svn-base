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
package com.funambol.email.util;


/**
 * @version $Id: Def.java,v 1.11 2008-06-24 11:15:08 testa Exp $
 */
public class Def {

    public static final String JNDI_FNBL_CORE = "jdbc/fnblcore";
    public static final String JNDI_FNBL_USER = "jdbc/fnbluser";

    public static final String LOGGER_NAME = "funambol.email";

    public static final String TRAFFIC_LOGGER_NAME = "funambol.email.traffic";

    // bean configuration file for the inboxlistener task
    public static final String DEFAULT_INBOX_LISTENER_BEAN_FILE =
            "com/funambol/email/inboxlistener/task/InboxListenerTask.xml";
    // default id of the first Inbox-Listner
    public static final long DEFAULT_INBOX_LISTENER_ID = 0;

    /**
     * this constant must be set using the information about the
     * syncml client.
     * At the moment we have
     * - windows mobile plug-in for mobile outlook
     * - funambol j2me syncml email client
     * (these are only mobile devices)
     */
    public static final boolean IS_DEVICE = true;

    // separetor for the GUID creation
    public static final String SEPARATOR_FIRST  = "/";
    public static final String SEPARATOR_SECOND = "-FUN-"; // must be weird

    // prefix for the thread in the inbox-listerner
    public static final String IL_PREFIX = "IL-";

    public static final String TYPE_EMAIL  = "application/vnd.omads-email+xml";
    public static final String TYPE_FOLDER = "application/vnd.omads-folder+xml";

    public static final String ID_COUNTER_SENT       = "email.sentid";
    public static final String ID_COUNTER_FOLDER     = "email.folderid";
    public static final String ID_COUNTER_INBOX      = "email.cacheid";
    public static final String ID_COUNTER_MAILSERVER = "email.mailserverid";

    // max number of emails in the inbox cache
    public static final int MAX_INBOX_EMAIL_NUMBER  = 100;
    // max number of sent folder emails
    public static final int MAX_SENT_EMAIL_NUMBER  = 20;

    // polling time for the cache (u.m. minutes)
    public static final int POLLING_INTERVALL = 10;

    // protocols
    public static final String PROTOCOL_IMAP = "imap";
    public static final String PROTOCOL_POP3 = "pop3";

    //protocols default ports
    public static final int PROTOCOL_IMAP_PORT = 143;
    public static final int PROTOCOL_POP3_PORT = 110;

    //folder IDs
    public static final String FOLDER_ROOT_ID   = "ROOT";
    public static final String FOLDER_INBOX_ID  = "I";
    public static final String FOLDER_OUTBOX_ID = "O";
    public static final String FOLDER_SENT_ID   = "S";
    public static final String FOLDER_DRAFTS_ID = "D";
    public static final String FOLDER_TRASH_ID  = "T";

    //folder GUIDs
    public static final String FOLDER_ROOT_GUID = "ROOT";
    public static final String FOLDER_INBOX_GUID =
            FOLDER_ROOT_ID + SEPARATOR_FIRST + FOLDER_INBOX_ID;
    public static final String FOLDER_OUTBOX_GUID =
            FOLDER_ROOT_ID + SEPARATOR_FIRST + FOLDER_OUTBOX_ID;
    public static final String FOLDER_SENT_GUID =
            FOLDER_ROOT_ID + SEPARATOR_FIRST + FOLDER_SENT_ID;
    public static final String FOLDER_DRAFTS_GUID =
            FOLDER_ROOT_ID + SEPARATOR_FIRST + FOLDER_DRAFTS_ID;
    public static final String FOLDER_TRASH_GUID =
            FOLDER_ROOT_ID + SEPARATOR_FIRST + FOLDER_TRASH_ID;

    //default folder names
    public static final String FOLDER_ROOT_ENG   = "";
    public static final String FOLDER_INBOX_ENG  = "Inbox";
    public static final String FOLDER_OUTBOX_ENG = "Outbox";
    public static final String FOLDER_SENT_ENG_EXC = "Sent Items"; // Exchange
    public static final String FOLDER_SENT_ENG     = "Sent";
    public static final String FOLDER_DRAFTS_ENG = "Drafts";
    public static final String FOLDER_TRASH_ENG_EXC = "Deleted Items"; // Exchange
    public static final String FOLDER_TRASH_ENG     = "Trash";


    //folder roles
    public static final String FOLDER_INBOX_ROLE  = "inbox";
    public static final String FOLDER_OUTBOX_ROLE = "outbox";
    public static final String FOLDER_SENT_ROLE   = "sent";
    public static final String FOLDER_DRAFTS_ROLE = "drafts";
    public static final String FOLDER_TRASH_ROLE  = "deleted";

    //NOKIA folder names
    public static final String NOKIA_INBOX_ENG  = "Remote Inbox";
    public static final String NOKIA_OUTBOX_ENG = "Remote Outbox";
    public static final String NOKIA_SENT_ENG   = "Remote Sent";
    public static final String NOKIA_DRAFTS_ENG = "Remote Drafts";


    // filter
    public static final String FILTER_TYPE_INC   = "inclusive";
    public static final String FILTER_TYPE_EXC   = "exclusive";
    // time filter field
    public static final String FILTER_TIME_FIELD = "modified";
    // id filter field
    public static final String FILTER_ID_FIELD   = "LUID";
    // size filter field
    public static final String FILTER_SIZE_LABEL_HEADER = "emailitem";
    public static final String FILTER_SIZE_LABEL_BODY   = "texttype";
    public static final String FILTER_SIZE_LABEL_ATTACH = "attachtype";
    // size filter values
    //  0 0 0 0 0 1 = 1   FILTER_SIZE_H         (get only header)
    //  0 0 0 1 0 1 = 5   FILTER_SIZE_H_B       (get header and body)
    //  0 0 1 1 1 1 = 15  FILTER_SIZE_H_BPERC
    //  0 1 0 1 0 1 = 21  FILTER_SIZE_H_B_A     (get all message)
    //  1 1 1 1 1 1 = 63  FILTER_SIZE_H_B_APERC
    public static final int FILTER_SIZE_H          = 1;
    public static final int FILTER_SIZE_H_B        = 5;
    public static final int FILTER_SIZE_H_B_PERC   = 15;
    public static final int FILTER_SIZE_H_B_A      = 21;
    public static final int FILTER_SIZE_H_B_A_PERC = 63;
    // folder filter values
    //  0 0 0 0 1 -->  1  inbox
    //  0 0 0 1 1 -->  3  inbox + outbox
    //  0 0 1 1 1 -->  7  inbox + outbox + sent
    //  0 1 1 1 1 -->  15 inbox + outbox + sent + draft
    //  1 1 1 1 1 -->  31 inbox + outbox + sent + draft + trash
    public static final int FILTER_FOLDER_I     = 1;
    public static final int FILTER_FOLDER_IO    = 3;
    public static final int FILTER_FOLDER_IOS   = 7;
    public static final int FILTER_FOLDER_IOSD  = 15;
    public static final int FILTER_FOLDER_IOSDT = 31;

    // Header label
    public static final String HEADER_MESSAGE_ID   = "Message-ID";
    public static final String HEADER_RECEIVED     = "Received";
    public static final String HEADER_DATE         = "Date";
    public static final String HEADER_FROM         = "From";
    public static final String HEADER_TO           = "To";
    public static final String HEADER_REPLY_TO     = "Reply-To";
    public static final String HEADER_CC           = "CC";
    public static final String HEADER_BCC          = "BCC";
    public static final String HEADER_SUBJECT      = "Subject";
    public static final String HEADER_MIME         = "MIME-Version";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CHARSET      = "charset";

    public static final String HEADER_SYNCMESSAGE  = "X-Funambol";
    public static final String HEADER_PRIORITY     = "X-Priority";

    public static final String HEADER_CONTENT_TRANSFERTENC = "Content-Transfer-Encoding";

    public static final String HEADER_VALUE_SYNCMESSAGE = "true";

    public static final String[] HEADER_MINIMUM   = {
        HEADER_MESSAGE_ID,
        HEADER_DATE,
        HEADER_RECEIVED,
        HEADER_FROM,
        HEADER_TO,
        HEADER_SUBJECT,
        HEADER_MIME,
        HEADER_SYNCMESSAGE,
        HEADER_REPLY_TO,
        HEADER_CC,
        HEADER_BCC,
        HEADER_PRIORITY
    };

    // default Content for a mail
    public static final String CONTENT_BODY                 = "";
    public static final String CONTENT_BODY_ALERT           = 
            "Unsupported encoding for message body. " +
            "The system cannot download the content of this email.";
    public static final String CONTENT_CONTENTTYPE          = "text/plain; charset=UTF-8";
    public static final String CONTENT_CONTENTTYPE_HTML     = "text/html; charset=UTF-8";    
    public static final String CONTENT_TRANSFERTENC         = "8bit";
    public static final String CONTENT_CONTENTTYPE_RFC822   = "MESSAGE/RFC822";

    // message encoding types
    public static final String ENCODE_QUOTED_PRINTABLE = "quoted-printable";
    public static final String ENCODE_BASE64           = "base64";

    public static final String PWD_ENCODE_UTF8         = "UTF8";
    public static final String PWD_ALGORITHM           = "DESede";

    // server types
    public static final String SERVER_TYPE_EXCHANGE   = "Exchange";
    public static final String SERVER_TYPE_COURIER    = "Courier";
    public static final String SERVER_TYPE_GMAIL      = "GMail";
    public static final String SERVER_TYPE_AOL        = "AOL";
    public static final String SERVER_TYPE_HOTMAIL    = "Hotmail";
    public static final String SERVER_TYPE_OTHER      = "Other";


    public static final String TRAILER = "Funambol :: mobile open source :: http://www.funambol.com";

    //
    // error code for the mail server connection
    // ok                           = 0
    // invalid protocol             = 1
    // invalid username or password = 2
    // cannection failed            = 3
    public static final int ERR_OK                  = 0;
    public static final int ERR_INVALID_PROTOCOL    = 1;
    public static final int ERR_INVALID_CREDENTIALS = 2;
    public static final int ERR_CONNECTION_FAILED   = 3;
    public static final int ERR_INVALID_INPUT       = 4;
    public static final int ERR_INVALID_DOMAIN      = 5;
    public static final int ERR_GENERIC_MAILSERVER  = 6;

    public static final int ERR_SSL                 = 7;
    public static final int ERR_STORE_TIMEOUT       = 8;
    public static final int ERR_POP_NOT_ENABLED     = 9;
    public static final int ERR_UKNOWN_HOST         = 10;

    //
    // error filtering email
    public static final String ERR_FILTERING_EMAIL   = "Error Getting/Filtering Email";
    //
    // error code for the mail server deletion
    public static final int ERR_SERVER_DELETION = -20;

    // number of mail messages to be retrivied over the max number
    public static final int MAX_MAIL_NUMBER_OFFSET = 2;

    // total size of a udp packet (taht is: prefix + useful data + postfix).
    public static final int PACKET_SIZE = 256;

    // terminator of data witin the message body
    // @todo: should be moved to the ReaderPlugin.xml
    public static final byte MESSAGE_TERMINATOR = '\0';

    // string used to represent the forwarded flag by a mail server
    public static final String IS_FORWARDED_FLAG = "Forwarded";

    //
    // regular expressions used to identify mail server errors
    //
    public static final String ERR_REGEXP_LOGIN_FAILED    = ".*(invalid|username|password|((login|LOGIN).*failed)).*";
    public static final String ERR_REGEXP_SSL_ERROR       = ".*(ssl|SSL).*";
    public static final String ERR_REGEXP_TIME_OUT        = ".*time.*out.*";
    public static final String ERR_REGEXP_POP_NOT_ENABLED = ".*enable.*(POP|pop).*access.*";
    public static final String ERR_REGEXP_DOMAIN_INVALID  = ".*(domain|invalid).*";

    //
    // default timeouts values
    //
    public static final String DEFAULT_TIMEOUT_STORE      = "300000";
    public static final String DEFAULT_TIMEOUT_CONNECTION = "15000";
    
    //
    // Sockets factories
    //
    public static final String SSL_SOCKET_FACTORY_STANDARD = "javax.net.ssl.SSLSocketFactory";
    public static final String SSL_SOCKET_FACTORY_FUNAMBOL = "com.funambol.email.ssl.FunambolSSLSocketFactory";
    
    //
    // sequence used to generate unique tokens
    //
    public static final String TOKEN_SEQUENCE = "email.token";
}



