--
-- Initialization data for the PIM module
--
-- @version $Id: create_schema.sql,v 1.1 2008-04-29 07:41:00 piter_may Exp $
--

-- table for contact data

create table fnbl_pim_contact (
    id              bigint PRIMARY KEY,
    userid          varchar(255),
    last_update     bigint,
    status          char,
    photo_type      smallint,

-- contact details
    importance      smallint,
    sensitivity     smallint,
    subject         varchar(255),
    folder          varchar(255),

-- personal details
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

-- business details
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

-- table for extra contact data

create table fnbl_pim_contact_item (
    contact      bigint,
    type         smallint,
    value        varchar(255),

    PRIMARY KEY (contact, type),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact 
                          ON DELETE CASCADE
);

-- table for contact address data

create table fnbl_pim_address (
    contact      bigint, 
    type         smallint,
    street       varchar(128),
    city         varchar(64),
    state        varchar(64),
    postal_code  varchar(16),
    country      varchar(32),
    po_box       varchar(16),

    PRIMARY KEY (contact, type),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact 
                          ON DELETE CASCADE
);

-- table for the contact photo

create table fnbl_pim_contact_photo (
    contact      bigint,
    type         varchar(64),
    photo        binary,
    url          varchar(255),

    PRIMARY KEY (contact),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact
                          ON DELETE CASCADE
);

-- table for event data

create table fnbl_pim_calendar (
    id                     bigint PRIMARY KEY,
    userid                 varchar(255),
    last_update            bigint,
    status                 char,
    type                   smallint,

-- common event and task details
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

-- event details
    reply_time             timestamp,

-- task details
    completed              timestamp,
    percent_complete       smallint
);

-- table for exceptions to recurrence rules

create table fnbl_pim_calendar_exception (
    calendar        bigint,
    addition        char(1),
    occurrence_date timestamp,

    PRIMARY KEY (calendar, addition, occurrence_date),
    FOREIGN KEY (calendar) REFERENCES fnbl_pim_calendar 
                           ON DELETE CASCADE
);


-- Table: fnbl_push_listener_registry

create table fnbl_push_listener_registry
(
  id               bigint PRIMARY KEY,
  period           bigint,
  active           char(1),
  last_update      bigint,
  status           varchar(1),
  task_bean_file   varchar(255)
);


-- Table: fnbl_pim_listener_registry

create table fnbl_pim_listener_registry
(
  id               bigint PRIMARY KEY,
  username         varchar(255),
  push_contacts    char(1),
  push_calendars   char(1),
  push_notes       char(1),

  FOREIGN KEY (id) REFERENCES fnbl_push_listener_registry
                   ON UPDATE NO ACTION ON DELETE CASCADE
);

create index ind_pim_contact  on fnbl_pim_contact  (userid, last_update, status);
create index ind_pim_calendar on fnbl_pim_calendar (userid, last_update, status);

CREATE TABLE fnbl_pim_note (
   id              bigint NOT NULL,
   userid          character varying(255),
   last_update     bigint,
   status          character(1),
   subject         varchar,
   textdescription varchar,
   categories      varchar,
   folder          varchar,
   color           integer,
   height          integer,
   width           integer,
   top             integer,
   leftmargin      integer,
       CONSTRAINT fnbl_pim_note_pkey PRIMARY KEY (id)
);    

