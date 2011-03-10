--
-- This script contains the ddl to create the engine database.
--
-- @version $Id: create_user_schema.sql 36662 2011-02-17 18:05:07Z luigiafassina $
--
create table fnbl_client_mapping (
  principal   bigint       not null,
  sync_source varchar(128) not null,
  luid        varchar(200) not null,
  guid        varchar(200) not null,
  last_anchor varchar(20),

  constraint pk_clientmapping primary key (principal, sync_source, luid, guid)

);

create table fnbl_pim_contact (
    id              bigint PRIMARY KEY,
    userid          varchar(255),
    last_update     bigint,
    status          char,
    photo_type      smallint,
    importance      smallint,
    sensitivity     smallint,
    subject         varchar(255),
    folder          varchar(255),
    anniversary     varchar(16),
    first_name      varchar(64),
    middle_name     varchar(64),
    last_name       varchar(64),
    display_name    varchar(128),
    birthday        varchar(16),
    body            varchar(255),
    categories      varchar(255),
    children        varchar(255),
    hobbies         varchar(255),
    initials        varchar(16),
    languages       varchar(255),
    nickname        varchar(64),
    spouse          varchar(128),
    suffix          varchar(32),
    title           varchar(32),
    gender          char(1),
    assistant       varchar(128),
    company         varchar(255),
    department      varchar(255),
    job_title       varchar(128),
    manager         varchar(128),
    mileage         varchar(16),
    office_location varchar(64),
    profession      varchar(64),
    companies       varchar(255)
);

create table fnbl_pim_contact_item (
    contact      bigint,
    type         smallint,
    value        varchar(255),

    PRIMARY KEY (contact, type)
);

create table fnbl_pim_address (
    contact      bigint,
    type         smallint,
    street       varchar(128),
    city         varchar(64),
    state        varchar(64),
    postal_code  varchar(16),
    country      varchar(32),
    po_box       varchar(16),
    extended_address varchar(255),

    PRIMARY KEY (contact, type)
);

create table fnbl_pim_contact_photo (
    contact      bigint,
    type         varchar(64),
    photo        binary,
    url          varchar(255),

    PRIMARY KEY (contact)
);

create table fnbl_pim_calendar (
    id                     bigint PRIMARY KEY,
    userid                 varchar(255),
    last_update            bigint,
    status                 char,
    type                   smallint,
    all_day                char(1),
    body                   varchar(255),
    busy_status            smallint,
    categories             varchar(255),
    companies              varchar(255),
    birthday               varchar(16),
    duration               integer,
    dstart                 timestamp,
    dend                   timestamp,
    folder                 varchar(255),
    importance             smallint,
    location               varchar(255),
    meeting_status         smallint,
    mileage                varchar(16),
    reminder_time          timestamp,
    reminder               char(1),
    reminder_sound_file    varchar(255),
    reminder_options       integer,
    reminder_repeat_count  integer,
    sensitivity            smallint,
    subject                varchar(1000),
    rec_type               smallint,
    rec_interval           integer,
    rec_month_of_year      smallint,
    rec_day_of_month       smallint,
    rec_day_of_week_mask   varchar(16),
    rec_instance           smallint,
    rec_start_date_pattern varchar(32),
    rec_no_end_date        char(1),
    rec_end_date_pattern   varchar(32),
    rec_occurrences        smallint,
    dstart_tz              varchar(255),
    dend_tz                varchar(255),
    reminder_tz            varchar(255),
    reply_time             timestamp,
    completed              timestamp,
    percent_complete       smallint
);

create table fnbl_pim_calendar_exception (
    calendar        bigint,
    addition        char(1),
    occurrence_date timestamp,

    PRIMARY KEY (calendar, addition, occurrence_date)
);

create index ind_pim_contact on fnbl_pim_contact (userid, last_update, status);
create index ind_pim_calendar on fnbl_pim_calendar(userid, last_update, status);

CREATE TABLE fnbl_pim_note (
   id              bigint NOT NULL,
   userid          varchar(255),
   last_update     bigint,
   status          character(1),
   crc             bigint,
   subject         varchar(255),
   textdescription varchar(65535),
   categories      varchar(255),
   folder          varchar(255),
   color           integer,
   height          integer,
   width           integer,
   top             integer,
   leftmargin      integer,
       CONSTRAINT fnbl_pim_note_pkey PRIMARY KEY (id)
);

create table fnbl_file_data_object (
    id              bigint PRIMARY KEY,
    userid          varchar(255),
    source_uri      varchar(255),
    last_update     bigint,
    status          char(1),
    upload_status   char(1),
    local_name      varchar(255),
    crc             bigint,
    true_name       varchar(255),
    created         timestamp,
    modified        timestamp,
    accessed        timestamp,
    h               char(1),
    s               char(1),
    a               char(1),
    d               char(1),
    w               char(1),
    r               char(1),
    x               char(1),
    cttype          varchar(255),
    object_size     bigint,
    size_on_storage bigint
);

create table fnbl_file_data_object_property (
    id     bigint PRIMARY KEY,
    fdo_id bigint,
    name   varchar(255),
    value  varchar(65535)
);

create table fnbl_device_config (
username     varchar(255) not null,
principal    bigint       not null,
uri          varchar(128) not null,
value        varchar(255) not null,
last_update  bigint       not null,
status       char         not null,
encrypted    boolean default false,
constraint pk_config primary key (username, principal, uri)
);
