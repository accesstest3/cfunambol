--
-- Creation of the Schema for the Email Connector module
--
-- @version $Id: create_schema.sql,v 1.4 2008-06-03 10:16:59 testa Exp $
--

-- tables for the caching system

create table fnbl_email_cache (
    guid       varchar(200) not null,
    source_uri varchar(128) not null,
    principal  bigint       not null,
    last_crc   bigint,
    invalid    char, 
    internal   char,    
    messageid  varchar(700),
    headerdate varchar(20),
    received   varchar(20),
    subject    varchar(700),
    sender     varchar(300),
    isemail    char,    
    constraint pk_cache primary key (guid, source_uri, principal),
    constraint fk_principal_email_cache foreign key (principal) 
        references fnbl_principal (id) on delete cascade
);

create table fnbl_email_inbox (
    guid           varchar(200) not null,
    username       varchar(50)  not null,
    protocol       varchar(4)   not null,
    last_crc       bigint,
    invalid        char, 
    internal       char,    
    messageid      varchar(700),
    headerdate     varchar(20),   
    received       varchar(20),
    subject        varchar(700),
    sender         varchar(300),
    token          varchar(200),
    status         char,
    constraint pk_cache_inbox primary key (guid, username, protocol)        
);

-- tables folder guids

create table fnbl_email_folder (
    guid       varchar(50)  not null,
    source_uri varchar(128) not null,
    principal  bigint       not null,
    parentid   varchar(50),
    path       varchar(500),
    constraint pk_folder primary key (guid, source_uri, principal),
    constraint fk_principal_email_folder foreign key (principal) 
        references fnbl_principal (id) on delete cascade            
);

-- tables for POP connector

create table fnbl_email_sentpop (
    id         varchar(200) not null,
    source_uri varchar(128) not null,
    principal  bigint       not null,
    messageid  varchar(700),
    mail       bytea,
    constraint pk_sentpop primary key (id, source_uri, principal),
    constraint fk_principal_email_sentpop foreign key (principal) 
        references fnbl_principal (id) on delete cascade                
);

-- V6 tables

create table fnbl_email_push_registry (
  id               bigint PRIMARY KEY,
  period           bigint,
  active           char(1),
  last_update      bigint,
  status           varchar(1),
  task_bean_file   varchar(255)
);

create table fnbl_email_enable_account (
    account_id    bigint       not null,
    username      varchar(50)  not null,
    constraint pk_enable_account primary key (account_id, username)        
);

create table fnbl_email_account (
    account_id    bigint       not null,
    username      varchar(50)  not null,
    ms_login      varchar(50)  not null,
    ms_password   varchar(50)  not null,
    ms_address    varchar(70)  not null,
    ms_mailboxname   varchar(64),
    push          char,
    soft_delete   char,    
    max_num_email integer,
    max_imap_email integer,
    mailserver_id varchar(20)  not null,
    server_public char,
    server_type   varchar(20),
    description   varchar(50),
    protocol      varchar(5),
    out_server    varchar(30),
    out_port      integer,
    out_auth      char,
    in_server     varchar(30),
    in_port       integer,
    sslin         char,
    sslout        char,
    inbox_name    varchar(30),
    inbox_active  char,
    outbox_name   varchar(30),
    outbox_active char,
    sent_name     varchar(30),
    sent_active   char,
    drafts_name   varchar(30),
    drafts_active char,
    trash_name    varchar(30),
    trash_active  char,
    out_login     varchar(50),
    out_password  varchar(50),    
    
    constraint pk_user_account primary key (account_id, username)
        
);

create table fnbl_email_mailserver (
    mailserver_id varchar(20)  not null,
    server_public char,
    server_type   varchar(20),
    description   varchar(50),
    protocol      varchar(5),
    out_server    varchar(30),
    out_port      integer,
    out_auth      char,
    in_server     varchar(30),
    in_port       integer,
    sslin         char,
    sslout        char,
    inbox_name    varchar(30),
    inbox_active  char,
    outbox_name   varchar(30),
    outbox_active char,
    sent_name     varchar(30),
    sent_active   char,
    drafts_name   varchar(30),
    drafts_active char,
    trash_name    varchar(30),
    trash_active  char,
    soft_delete   char,    
    constraint pk_mailserver primary key (mailserver_id)
);

create index ind_fnbl_email_inbox_user_prot on fnbl_email_inbox  (username, protocol);
create index ind_fnbl_email_inbox_token on fnbl_email_inbox (token);
create index ind_fnbl_email_enable_account_username on fnbl_email_enable_account (username);
create index ind_fnbl_email_push_registry_status on fnbl_email_push_registry (status);
create index ind_fnbl_email_push_registry_lastupdate_status on fnbl_email_push_registry (last_update, status);
create index ind_fnbl_email_cache_uri_principal on fnbl_email_cache (source_uri, principal);
create index ind_fnbl_email_folder_uri_principal on fnbl_email_folder (source_uri, principal);