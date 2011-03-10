--
-- Initialization data for the PIM module
--
-- @version $Id: create_schema.sql,v 1.1.1.1 2008-03-20 21:38:28 stefano_fornari Exp $
--

-- table for contact data

create table fnbl_pim_contact (
    id              bigint PRIMARY KEY,
    userid          varchar(255) binary,
    last_update     bigint,
    status          char,
    photo_type      smallint,

-- contact details
    importance      smallint,
    sensitivity     smallint,
    subject         varchar(255) binary,
    folder          varchar(255) binary,

-- personal details
    anniversary     varchar(16) binary,
    first_name      varchar(64) binary,
    middle_name     varchar(64) binary,
    last_name       varchar(64) binary,
    display_name    varchar(128) binary,
    birthday        varchar(16) binary,
    body            text,
    categories      varchar(255) binary,
    children        varchar(255) binary,
    hobbies         varchar(255) binary,
    initials        varchar(16) binary,
    languages       varchar(255) binary,
    nickname        varchar(64) binary,
    spouse          varchar(128) binary,
    suffix          varchar(32) binary,
    title           varchar(32) binary,
    gender          char(1),

-- business details
    assistant       varchar(128) binary,
    company         varchar(255) binary,
    department      varchar(255) binary,
    job_title       varchar(128) binary,
    manager         varchar(128) binary,
    mileage         varchar(16) binary,
    office_location varchar(64) binary,
    profession      varchar(64) binary,
    companies       varchar(255) binary
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for extra contact data

create table fnbl_pim_contact_item (
    contact      bigint,
    type         smallint,
    value        varchar(255) binary,

    PRIMARY KEY (contact, type),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for contact address data
create table fnbl_pim_address (
    contact          bigint,
    type             smallint,
    street           varchar(128) binary,
    city             varchar(64) binary,
    state            varchar(64) binary,
    postal_code      varchar(16) binary,
    country          varchar(32) binary,
    po_box           varchar(16) binary,
    extended_address varchar(255) binary,

    PRIMARY KEY (contact, type),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for the contact photo

create table fnbl_pim_contact_photo (
    contact      bigint,
    type         varchar(64) binary,
    photo        longblob,
    url          varchar(255),

    PRIMARY KEY (contact),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for calendar data

create table fnbl_pim_calendar (
    id                    bigint PRIMARY KEY,
    userid                varchar(255) binary,
    last_update           bigint,
    status                char,
    type                  smallint,

-- common event and task details
    all_day                char(1),
    body                   text,
    busy_status            smallint,
    categories             varchar(255) binary,
    companies              varchar(255) binary,
    birthday               varchar(16) binary,
    duration               integer,
    dstart                 datetime,
    dend                   datetime,
    folder                 varchar(255) binary,
    importance             smallint,
    location               varchar(255) binary,
    meeting_status         smallint,
    mileage                varchar(16) binary,
    reminder_time          datetime,
    reminder               char(1),
    reminder_sound_file    varchar(255) binary,
    reminder_options       integer,
    reminder_repeat_count  integer,
    sensitivity            smallint,
    subject                varchar(1000) binary,
    rec_type               smallint,
    rec_interval           integer,
    rec_month_of_year      smallint,
    rec_day_of_month       smallint,
    rec_day_of_week_mask   varchar(16) binary,
    rec_instance           smallint,
    rec_start_date_pattern varchar(32) binary,
    rec_no_end_date        char(1),
    rec_end_date_pattern   varchar(32) binary,
    rec_occurrences        smallint,
    dstart_tz              varchar(255) binary,
    dend_tz                varchar(255) binary, 
    reminder_tz            varchar(255) binary,

-- event details
    reply_time             datetime,

-- task details
    completed              datetime,
    percent_complete       smallint
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for exceptions to recurrence rules

create table fnbl_pim_calendar_exception (
    calendar        bigint,
    addition        char(1),
    occurrence_date datetime,

    PRIMARY KEY (calendar, addition, occurrence_date),
    FOREIGN KEY (calendar) REFERENCES fnbl_pim_calendar (id)
                           ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

-- Table: fnbl_push_listener_registry

create table fnbl_push_listener_registry
(
  id               bigint PRIMARY KEY,
  period           bigint,
  active           char(1),
  last_update      bigint,
  status           varchar(1) binary,
  task_bean_file   varchar(255) binary
)ENGINE = InnoDB
CHARACTER SET utf8;

-- Table: fnbl_pim_listener_registry

create table fnbl_pim_listener_registry
(
  id             bigint PRIMARY KEY,
  username       varchar(255) binary,
  push_contacts  char(1),
  push_calendars char(1),
  push_notes     char(1),

  FOREIGN KEY (id) REFERENCES fnbl_push_listener_registry (id)
                   ON UPDATE NO ACTION ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

-- Table: fnbl_pim_note

create table fnbl_pim_note (
   id              bigint PRIMARY KEY,
   userid          varchar(255) binary,
   last_update     bigint,
   status          char(1),
   crc             bigint,
   subject         varchar(255) binary,
   textdescription text(65535),
   categories      varchar(255) binary,
   folder          varchar(255) binary,
   color           integer,
   height          integer,
   width           integer,
   top             integer,
   leftmargin      integer
)ENGINE = InnoDB
CHARACTER SET utf8;

-- Table: fnbl_file_data_object

create table fnbl_file_data_object (
    id              bigint PRIMARY KEY,
    userid          varchar(255) binary,
    source_uri      varchar(255) binary,
    last_update     bigint,
    status          char,
    upload_status   char,
    local_name      varchar(255) binary,
    crc             bigint,
    true_name       varchar(255) binary,
    created         datetime,
    modified        datetime,
    accessed        datetime,
    h               char(1),
    s               char(1),
    a               char(1),
    d               char(1),
    w               char(1),
    r               char(1),
    x               char(1),
    cttype          varchar(255) binary,
    object_size     bigint,
    size_on_storage bigint
)ENGINE = InnoDB
CHARACTER SET utf8;

-- Table: fnbl_file_data_object_property

create table fnbl_file_data_object_property (
    id     bigint PRIMARY KEY,
    fdo_id bigint,
    name   varchar(255) binary,
    value  text,
    FOREIGN KEY (fdo_id) REFERENCES fnbl_file_data_object(id) ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

create index ind_push_listener_status            on fnbl_push_listener_registry (status);
create index ind_push_listener_lastupdate_status on fnbl_push_listener_registry (last_update, status);
create index ind_pim_listener_username           on fnbl_pim_listener_registry  (username);

create index ind_pim_contact  on fnbl_pim_contact (userid, last_update, status);

create index ind_pim_calendar on fnbl_pim_calendar(userid, last_update, status);
create index ind_pim_calendar_userid_dstart on fnbl_pim_calendar (userid, dstart);

create index ind_pim_note     on fnbl_pim_note    (userid, last_update, status);

create index ind_file_data_object on fnbl_file_data_object (userid, source_uri, last_update, status);
