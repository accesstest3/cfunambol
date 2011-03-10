--
-- Creation of the Schema for the Email Connector module
--
-- @version $Id: create_schema.sql,v 1.4 2008-06-03 10:16:59 testa Exp $
--

-- tables for the caching system

create table fnbl_email_cache (
    guid       varchar(200) binary not null,
    source_uri varchar(128) binary not null,
    principal  bigint       not null,
    last_crc   bigint,
    invalid    char,
    internal   char,
    messageid  varchar(700) binary,
    headerdate varchar(20)  binary,
    received   varchar(20)  binary,
    subject    varchar(700) binary,
    sender     varchar(300) binary,
    isemail    char,
    constraint pk_cache primary key (guid, source_uri, principal),
    constraint fk_principal_email_cache foreign key (principal)
        references fnbl_principal (id) on delete cascade
)ENGINE = InnoDB
CHARACTER SET utf8;

create table fnbl_email_inbox (
    guid           varchar(200) binary not null,
    username       varchar(50)  binary not null,
    protocol       varchar(4)   binary not null,
    last_crc       bigint,
    invalid        char,
    internal       char,
    messageid      varchar(700) binary,
    headerdate     varchar(20)  binary,
    received       varchar(20)  binary,
    subject        varchar(700) binary,
    sender         varchar(300) binary,
    token          varchar(200) binary,
    status         char,
    constraint pk_cache_inbox primary key (guid, username, protocol)
)ENGINE = InnoDB
CHARACTER SET utf8;

-- tables folder guids

create table fnbl_email_folder (
    guid       varchar(50)  binary not null,
    source_uri varchar(128) binary not null,
    principal  bigint       not null,
    parentid   varchar(50)  binary,
    path       varchar(500) binary,
    constraint pk_folder primary key (guid, source_uri, principal),
    constraint fk_principal_email_folder foreign key (principal)
        references fnbl_principal (id) on delete cascade    
)ENGINE = InnoDB
CHARACTER SET utf8;

-- tables for POP connector

create table fnbl_email_sentpop (
    id         varchar(200) binary not null,
    source_uri varchar(128) binary not null,
    principal  bigint       not null,
    messageid  varchar(700) binary,
    mail       mediumblob,
    constraint pk_sentpop primary key (id, source_uri, principal),
    constraint fk_principal_email_sentpop foreign key (principal)
        references fnbl_principal (id) on delete cascade    
)ENGINE = InnoDB
CHARACTER SET utf8;

-- V6 tables

create table fnbl_email_push_registry (
  id               bigint PRIMARY KEY,
  period           bigint,
  active           char(1),
  last_update      bigint,
  status           varchar(1) binary,
  task_bean_file   varchar(255) binary
)ENGINE = InnoDB
CHARACTER SET utf8;

create table fnbl_email_enable_account (
    account_id    bigint not null,
    username      varchar(50) binary  not null,
    constraint pk_enable_account primary key (account_id, username)
)ENGINE = InnoDB
CHARACTER SET utf8;

create table fnbl_email_account (
    account_id    bigint  not null,
    username      varchar(50) binary  not null,
    ms_login      varchar(50) binary  not null,
    ms_password   varchar(50) binary  not null,
    ms_address    varchar(70) binary  not null,
    ms_mailboxname   varchar(64) binary,
    push          char,
    soft_delete   char,
    max_num_email integer,
    max_imap_email integer,
    mailserver_id varchar(20) binary  not null,
    server_public char,
    server_type   varchar(20) binary,
    description   varchar(50) binary,
    protocol      varchar(5)  binary,
    out_server    varchar(30) binary,
    out_port      integer,
    out_auth      char,
    in_server     varchar(30) binary,
    in_port       integer,
    sslin         char,
    sslout        char,
    inbox_name    varchar(30) binary,
    inbox_active  char,
    outbox_name   varchar(30) binary,
    outbox_active char,
    sent_name     varchar(30) binary,
    sent_active   char,
    drafts_name   varchar(30) binary,
    drafts_active char,
    trash_name    varchar(30) binary,
    trash_active  char,
    out_login     varchar(50) binary,
    out_password  varchar(50) binary,

    constraint pk_user_account primary key (account_id, username)

)ENGINE = InnoDB
CHARACTER SET utf8;

create table fnbl_email_mailserver (
    mailserver_id varchar(20)  binary not null,
    server_public char,
    server_type   varchar(20) binary,
    description   varchar(50) binary,
    protocol      varchar(5)  binary,
    out_server    varchar(30) binary,
    out_port      integer,
    out_auth      char,
    in_server     varchar(30) binary,
    in_port       integer,
    sslin         char,
    sslout        char,
    inbox_name    varchar(30) binary,
    inbox_active  char,
    outbox_name   varchar(30) binary,
    outbox_active char,
    sent_name     varchar(30) binary,
    sent_active   char,
    drafts_name   varchar(30) binary,
    drafts_active char,
    trash_name    varchar(30) binary,
    trash_active  char,
    soft_delete   char,
    constraint pk_mailserver primary key (mailserver_id)
)ENGINE = InnoDB
CHARACTER SET utf8;

create index ind_fnbl_email_inbox_user_prot on fnbl_email_inbox  (username, protocol);
create index ind_fnbl_email_inbox_token on fnbl_email_inbox (token);
create index ind_fnbl_email_enable_account_username on fnbl_email_enable_account (username);
create index ind_fnbl_email_push_registry_status on fnbl_email_push_registry (status);
create index ind_fnbl_email_push_registry_lastupdate_status on fnbl_email_push_registry (last_update, status);
create index ind_fnbl_email_cache_uri_principal on fnbl_email_cache (source_uri, principal);
create index ind_fnbl_email_folder_uri_principal on fnbl_email_folder (source_uri, principal);