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
package com.funambol.pimlistener.service;

/**
 * Constants to handle pim tables, fnbl_last_sync table and listeners registry
 * tables.
 *
 * @version $Id$
 */
public interface FnblDatabaseConstants {

    // ---------------------------------------------------------- fnbl_last_sync
    public static final String DROP_FNBL_LAST_SYNC =
        "drop table fnbl_last_sync";

    public static final String CREATE_FNBL_LAST_SYNC =
        "create table fnbl_last_sync (" +
        "principal   bigint  not null," +
        "sync_source varchar(128) not null," +
        "start_sync  bigint," +
        "constraint pk_last_sync primary key (principal, sync_source))";

    // ---------------------------------------------- fnbl_pim_listener_registry
    public static final String DROP_FNBL_PIM_LISTENER_REGISTRY =
        "drop table fnbl_pim_listener_registry";

    public static final String CREATE_FNBL_PIM_LISTENER_REGISTRY =
        "create table fnbl_pim_listener_registry (" +
        "id             bigint PRIMARY KEY," +
        "username       varchar(255)," +
        "push_contacts  char(1)," +
        "push_calendars char(1)," +
        "push_notes     char(1))";

    // --------------------------------------------- fnbl_push_listener_registry
    public static final String DROP_FNBL_PUSH_LISTENER_REGISTRY =
        "drop table fnbl_push_listener_registry";
    
    public static final String CREATE_FNBL_PUSH_LISTENER_REGISTRY =
        "create table fnbl_push_listener_registry (" +
        "id             bigint PRIMARY KEY," +
        "period         bigint," +
        "active         char(1)," +
        "last_update    bigint," +
        "status         varchar(1)," +
        "task_bean_file varchar(255))";

    // -------------------------------------------------------- fnbl_pim_contact
    public static final String DROP_FNBL_PIM_CONTACT  =
        "drop table fnbl_pim_contact";

    public static final String CREATE_FNBL_PIM_CONTACT =
        "create table fnbl_pim_contact (" +
        "id          bigint PRIMARY KEY," +
        "userid      varchar(255)," +
        "last_update bigint," +
        "first_name  varchar(64))";

    // ------------------------------------------------------- fnbl_pim_calendar
    public static final String DROP_FNBL_PIM_CALENDAR =
        "drop table fnbl_pim_calendar";

    public static final String CREATE_FNBL_PIM_CALENDAR =
        "create table fnbl_pim_calendar (" +
        "id          bigint PRIMARY KEY," +
        "userid      varchar(255)," +
        "last_update bigint," +
        "type        smallint," +
        "dstart      timestamp," +
        "dend        timestamp)";

    // ----------------------------------------------------------- fnbl_pim_note
    public static final String DROP_FNBL_PIM_NOTE =
        "drop table fnbl_pim_note";

    public static final String CREATE_FNBL_PIM_NOTE =
        "create table fnbl_pim_note (" +
        "id          bigint PRIMARY KEY," +
        "userid      character varying(255)," +
        "last_update bigint," +
        "subject     varchar(255))";

}
