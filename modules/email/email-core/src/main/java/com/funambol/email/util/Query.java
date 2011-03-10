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
 * Queries used by email connector and inbox listener.
 * <p/>
 * Note: if one of the following query strings will be used as a pattern for the
 * <code>MessageFormat.format</code> method then each single apex sign (') must
 * be replaced with the sequence '' in order to hold a single apex in the
 * resulting string.
 *
 * @version $Id: Query.java,v 1.5 2008-06-03 09:35:45 testa Exp $
 */
public class Query {

    /**
     *
     */
    public static final String CACHE_SELECT_ITEMS_BY_PRINCIPAL_BY_SOURCE =
            "select guid, last_crc, invalid, internal, messageid, " +
            " headerdate, received, subject, sender, isemail" +
            " from fnbl_email_cache where source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String CACHE_SELECT_INVALID_ITEMS =
            "select guid, last_crc, invalid, internal, messageid, " +
            " headerdate, received, subject, sender, isemail " +
            " from fnbl_email_cache where source_uri = ? and principal = ? and invalid = 'Y' ";

    /**
     *
     */
    public static final String CACHE_ADD_LOCAL_ITEM  =
            "insert into fnbl_email_cache " +
            "(guid, last_crc, invalid, internal, source_uri, principal, " +
            " messageid, headerdate, received, subject, sender, isemail) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     *
     */
    public static final String CACHE_DELETE_LOCAL_ITEMS  =
            "delete from fnbl_email_cache " +
            " where source_uri = ? and principal = ? ";

    public static final String CACHE_DELETE_ITEMS = 
            "delete from fnbl_email_cache where principal = ? ";
    /**
     *
     */
    public static final String SENT_DELETE_POP  =
            "delete from fnbl_email_sentpop " +
            " where source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String SENT_SELECT_TWIN_SENT =
            "select id, mail from fnbl_email_sentpop " +
            " where messageid = ? and source_uri = ? and principal = ?";

    /**
     * invalid item in cache query
     */
    public static final String CACHE_INVALID_ITEM  =
            "update fnbl_email_cache set invalid = 'Y' " +
            " where guid = ?  and source_uri = ? and principal = ?";

    /**
     * invalid item in cache query
     */
    public static final String CACHE_INVALID_ITEM_FROM_CLIENT  =
            "update fnbl_email_cache set invalid = 'Y' " +
            " where guid = ?  and source_uri = ? and principal = ? and isemail = 'Y'";

    /**
     * folders queries
     */
    public static final String FOLDER_INSERT_FOLDER =
            "insert into fnbl_email_folder " +
            "(guid, parentid, source_uri, principal, path) values (?,?,?,?,?)";

    /**
     *
     */
    public static final String FOLDER_DELETE_FOLDER =
            "delete from fnbl_email_folder " +
            " where guid = ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String FOLDER_SELECT_FOLDER =
            "select guid, parentid, path from fnbl_email_folder " +
            " where guid = ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String FOLDER_SELECT_FULLPATH =
            "select path from fnbl_email_folder " +
            " where guid = ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String FOLDER_SELECT_FULLPATH_FROM_FID =
            "select path from fnbl_email_folder " +
            " where guid like ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String FOLDER_SELECT_FOLDERID =
            "select guid, parentid from fnbl_email_folder " +
            " where source_uri = ? and principal = ? and upper(path) = upper(?) ";

    /**
     *
     */
    public static final String FOLDER_SELECT_ALL_FOLDER =
            "select guid, parentid, path from fnbl_email_folder " +
            " where source_uri = ? and principal = ?";

    /**
     * sent items queries
     */
    public static final String SENT_INSERT_SENT =
            "insert into fnbl_email_sentpop " +
            " (id, source_uri, principal, mail, messageid) values (?,?,?,?,?)";

    /**
     *
     */
    public static final String SENT_DELETE_SENT =
            "delete from fnbl_email_sentpop " +
            " where id = ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String SENT_SELECT_SENT =
            "select mail from fnbl_email_sentpop " +
            " where id = ? and source_uri = ? and principal = ?";

    /**
     *
     */
    public static final String SENT_SELECT_ALL_SENT =
            "select id from fnbl_email_sentpop " +
            " where source_uri = ? and principal = ? and id like ? ";


    /**
     *
     */
    public static final String INBOX_INSERT_EMAIL =
            "insert into fnbl_email_inbox " +
            " (username, protocol, guid, last_crc, invalid, internal, messageid, " +
            "   headerdate, received, subject, sender, status, token) " +
            " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     *
     */
    public static final String INBOX_UPDATE_CRC =
            "update fnbl_email_inbox set last_crc = ? " +
            "  where username = ? and protocol = ? and guid = ? ";

    /**
     *
     */
    public static final String INBOX_SELECT_ALL =
            " select guid, messageid, headerdate, received, subject, sender, " +
            "        last_crc, invalid, internal, status " +
            " from fnbl_email_inbox " +
            " where username=? and protocol=? order by received desc ";

    /**
     *
     */
    public static final String INBOX_SELECT_BY_GUID =
            " select guid, messageid, headerdate, received, subject, sender, " +
            "        last_crc, invalid, internal, status " +
            " from fnbl_email_inbox " +
            " where guid = ? ";

    /**
     *
     */
    public static final String INBOX_SELECT_UNDELETED_MAILBOX_INFO =
            " select guid, last_crc, invalid, internal, messageid, " +
            "        headerdate, received, subject, sender, status " +
            " from fnbl_email_inbox where username = ? " +
            " and protocol = ? and status <> 'D' " +
            " order by headerdate desc";
        
    /**
     *
     */
    public static final String INBOX_SET_DELETED_STATUS =
            " update fnbl_email_inbox set status = 'D' " +
            " where  username = ? and protocol = ? and guid = ?" ;

    /**
     *
     */
    public static final String INBOX_REMOVE_DELETED_STATUS =
            " update fnbl_email_inbox set status = 'N' " +
            " where  username = ? and protocol = ? " ;

    /**
     *
     */
    public static final String INBOX_DELETE_EMAILS  =
            "delete from fnbl_email_inbox where username = ?";

    /**
     *
     */
    public static final String INBOX_DELETE_EMAIL  =
            "delete from fnbl_email_inbox where guid = ? and username = ? and protocol = ? ";


    /**
     *
     */
    public static final String INBOX_UPDATE_EMAIL =
            "update fnbl_email_inbox set last_crc=? " +
            "  where guid = ? and username = ? and protocol = ? ";

    /**
     *
     */
    public static final String INBOX_GET_ITEM_BY_GUID =
            "select guid, username, protocol, last_crc, invalid, internal, messageid, " +
            "       headerdate, received, subject, sender, status " +
            "from fnbl_email_inbox where guid = ? ";
    
    /**
     * 
     */
    public static final String INBOX_GET_TOKEN =
            "select token " +
            "from fnbl_email_inbox where username = ? and guid = ? ";
    
    public static final String INBOX_GET_GUID_BY_TOKEN =
            "select guid from fnbl_email_inbox where token = ? ";

    public static final String INBOX_GET_MAX_RECEIVED =
        "select max(received) from fnbl_email_inbox where username=?";

    public static final String INBOX_GET_NUM_UNREAD_EMAIL =
        "select count(*) from " +
        "(select last_crc from fnbl_email_inbox where username = ? " +
        "order by received desc limit ? ) crcs " +
        "where last_crc IN (200862478, 93782092, 2038348370, 3441281626)";

    /**
     *
     */
    public static final String ORDERBY_MAILSERVER_ID = " order by mailserver_id";

    /**
     *
     */
    public static final String MS_LIST_MAILSERVER  =
            "select mailserver_id, server_type, description, protocol, out_server, out_port," +
            " out_auth, in_server, in_port, sslin, sslout, " +
            " inbox_name, inbox_active, outbox_name, outbox_active, sent_name, sent_active, " +
            " drafts_name, drafts_active, trash_name, trash_active, soft_delete, server_public " +
            " from fnbl_email_mailserver";

    /**
     *
     */
    public static final String MS_GET_MAILSERVER  =
            "select mailserver_id, server_type, description, protocol, out_server, out_port," +
            " out_auth, in_server, in_port, sslin, sslout, " +
            " inbox_name, inbox_active, outbox_name, outbox_active, sent_name, sent_active, " +
            " drafts_name, drafts_active, trash_name, trash_active, soft_delete, server_public " +
            " from fnbl_email_mailserver where mailserver_id = ?";

    /**
     *
     */
    public static final String MS_INSERT_MAILSERVER  =
            "insert into fnbl_email_mailserver (mailserver_id, server_type, description, protocol, " +
            "  out_server, out_port, out_auth, in_server, in_port, sslin, sslout, " +
            "  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active, " +
            "  drafts_name, drafts_active, trash_name, trash_active, soft_delete, server_public) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " ;

    /**
     *
     */
    public static final String MS_UPDATE_MAILSERVER  =
            "update fnbl_email_mailserver set server_type=?, description=?, protocol=?, " +
            "  out_server=?, out_port=?, out_auth=?, in_server=?, in_port=?, " +
            "  sslin=?, sslout=?, " +
            "  inbox_name=?,inbox_active=?, outbox_name=?,outbox_active=?, " +
            "  sent_name=?, sent_active=?, drafts_name=?, drafts_active=?, " +
            "  trash_name=?, trash_active=?, soft_delete=?, server_public=? where mailserver_id = ? ";

    /**
     *
     */
    public static final String MS_DELETE_MAILSERVER  =
            "delete from fnbl_email_mailserver where mailserver_id = ? ";

    /**
     *
     */
    public static final String MS_ACCOUNT_CHECK_USER  =
            "select count(*) from fnbl_email_account where mailserver_id = ?" ;


    /**
     *
     */
    public static final String MS_ACCOUNT_SELECT_ACCOUNT_TO_REMOVE  =
            "select account_id, username from fnbl_email_account " +
            " where username not in (select username from fnbl_user) and listener_id = ?";

    /**
     *
     */
    private static final String MS_ACCOUNT_QUERY =
            "select" +
            " fepr.id, fepr.period, fepr.active, " +
            " fepr.task_bean_file, fepr.last_update, fepr.status," +
            " fea.username, fea.ms_login, fea.ms_password, fea.ms_address, " +
            " fea.push, fea.max_num_email, fea.max_imap_email, " +
            " fea.mailserver_id, fea.server_type, fea.description, fea.protocol, " +
            " fea.out_server, fea.out_port, fea.out_auth, fea.in_server, fea.in_port, " +
            " fea.sslin, fea.sslout, " +
            " fea.inbox_name, fea.inbox_active, " +
            " fea.outbox_name, fea.outbox_active, " +
            " fea.sent_name, fea.sent_active, " +
            " fea.drafts_name, fea.drafts_active, " +
            " fea.trash_name, fea.trash_active, " +
            " fea.soft_delete, fea.server_public, fea.ms_mailboxname, " +
            " fea.out_login, fea.out_password " +
            " from {0} fepr, fnbl_email_account fea " ;

    /**
     *
     */
    public static final String MS_ACCOUNT_LIST_USER =
            MS_ACCOUNT_QUERY + " where fepr.id = fea.account_id and status != ''D'' " +
            "and exists (select * from fnbl_user users where users.username = fea.username) " ;

    /**
     * must be used with
     * MS_ACCOUNT_GET_USER_FROM_ID,
     * MS_ACCOUNT_GET_USER_FROM_EMAILADDRESS,
     * MS_ACCOUNT_GET_USER_FROM_MAILBOXNAME
     * MS_ACCOUNT_GET_USER_FROM_USERNAME
     */
    private static final String MS_ACCOUNT_GET_USER  =
            MS_ACCOUNT_QUERY + " where fepr.id = fea.account_id ";

    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_FROM_ID  =
            MS_ACCOUNT_GET_USER + " and fea.account_id = ? order by id" ;
    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_FROM_EMAILADDRESS  =
            MS_ACCOUNT_GET_USER + " and fea.ms_address = ? order by id" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_FROM_MAILBOXNAME  =
            MS_ACCOUNT_GET_USER + " and fea.ms_mailboxname = ? order by id" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_FROM_USERNAME  =
            MS_ACCOUNT_GET_USER + " and fea.username = ? order by id" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_ID  =
            "select account_id from fnbl_email_enable_account where username = ?" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_GET_USER_NAME  =
            "select username from fnbl_email_enable_account where account_id = ?" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_INSERT_USER  =
            "insert into fnbl_email_account (" +
            " account_id, username, ms_login, ms_password, ms_address," +
            " push, max_num_email, max_imap_email," +
            " mailserver_id, server_type, description, protocol, " +
            " out_server, out_port, out_auth, in_server, in_port, " +
            " sslin, sslout, " +
            " inbox_name, inbox_active, outbox_name, outbox_active, sent_name, sent_active, " +
            " drafts_name, drafts_active, trash_name, trash_active, soft_delete, server_public, " +
            " ms_mailboxname, out_login, out_password) " +
            " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " ;

    /**
     *
     */
    public static final String MS_ACCOUNT_ENABLER =
            "insert into fnbl_email_enable_account (account_id, username) values (?,?)";

    /**
     *
     */
    public static final String MS_ACCOUNT_UPDATE_USER  =
            "update fnbl_email_account set " +
            " ms_login=?, ms_password=?, ms_address=?," +
            " push=?, max_num_email=?, max_imap_email=?, " +
            " mailserver_id=?, server_type=?, description=?, protocol=?, " +
            " out_server=?, out_port=?, out_auth=?, in_server=?, in_port=?, " +
            " sslin=?, sslout=?, " +
            " inbox_name=?, inbox_active=?, outbox_name=?, outbox_active=?, sent_name=?, sent_active=?, " +
            " drafts_name=?, drafts_active=?, trash_name=?, trash_active=?, soft_delete=?, server_public=?, " +
            " ms_mailboxname = ?, out_login = ?, out_password = ? " +
            " where account_id = ? and username = ?" ;


    /**
     *
     */
    public static final String MS_ACCOUNT_UPDATE_MAILSERVER_4_USERS  =
            "update fnbl_email_account set " +
            " server_type=?, description=?, protocol=?, " +
            " out_server=?, out_port=?, out_auth=?, in_server=?, in_port=?, " +
            " sslin=?, sslout=?, soft_delete=?, server_public=? " +
            " where mailserver_id = ? " ;


    /**
     *
     */
    public static final String MS_ACCOUNT_DELETE_USER  =
            "delete from fnbl_email_account where account_id = ? " ;

    /**
     *
     */
    public static final String MS_ACCOUNT_DISABLE_USER  =
            "update {0} set active = ''N'', last_update = ?, status = ? where id = ?" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_ENABLE_USER  =
            "update {0} set active = ''Y'', last_update = ?, status = ? where id = ?" ;

    /**
     *
     */
    public static final String MS_ACCOUNT_DELETE_ENABLE_USER  =
            "delete from fnbl_email_enable_account where account_id = ? " ;
    
    public static final String GET_PRINCIPAL_IDS_BY_USERNAME =
            "select id from fnbl_principal where username = ? ";
    
    public static final String FOLDER_DELETE_ITEMS =
            "delete from fnbl_email_folder where principal = ? ";
    
    public static final String SENT_DELETE_POP_BY_PRINCIPAL =
            "delete from fnbl_email_sentpop where principal = ? ";
    
    public static final String DS_USER_EXISTS =
            "select * from fnbl_user where username = ? ";
}
