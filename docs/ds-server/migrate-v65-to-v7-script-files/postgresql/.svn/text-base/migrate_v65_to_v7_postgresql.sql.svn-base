--
-- DS-Server migration
--
create table fnbl_device_config (
  principal    bigint       not null,
  uri          varchar(128) not null,
  value        varchar(255) not null,
  last_update  bigint       not null,
  status       char         not null,
  constraint pk_config primary key (principal, uri)
);

--
-- Email connection
--
create index ind_fnbl_email_inbox_user_prot on fnbl_email_inbox  (username, protocol);
ALTER TABLE fnbl_email_inbox ADD COLUMN token varchar(200);

--
-- Foundation connector
--
ALTER TABLE fnbl_pim_calendar ADD COLUMN dstart_tz   varchar(255);
ALTER TABLE fnbl_pim_calendar ADD COLUMN dend_tz     varchar(255);
ALTER TABLE fnbl_pim_calendar ADD COLUMN reminder_tz varchar(255);

create table fnbl_pim_note (
   id              bigint PRIMARY KEY,
   userid          varchar(255),
   last_update     bigint,
   status          char(1),
   subject         varchar,
   textdescription varchar,
   categories      varchar,
   folder          varchar,
   color           integer,
   height          integer,
   width           integer,
   top             integer,
   leftmargin      integer
);

create index ind_pim_note on fnbl_pim_note (userid, last_update, status);

insert into fnbl_sync_source_type(id, description, class, admin_class)
values('note-foundation','Notes SyncSource','com.funambol.foundation.engine.source.PIMNoteSyncSource','com.funambol.foundation.admin.PIMNoteSyncSourceConfigPanel');

insert into fnbl_sync_source_type(id, description, class, admin_class)
values ('config-foundation','Configuration SyncSource','com.funambol.foundation.engine.source.ConfigSyncSource',null);

insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','config-foundation');

insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','note-foundation');

delete from fnbl_client_mapping where (sync_source='note' or sync_source='snote');

delete from fnbl_last_sync where (sync_source='note' or sync_source='snote');

delete from fnbl_sync_source where uri='snote';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('snote', 'foundation/foundation/note-foundation/SIFNoteSource.xml','snote','note-foundation');

delete from fnbl_sync_source where uri='note';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('note', 'foundation/foundation/note-foundation/PlainTextNoteSource.xml','note','note-foundation');

insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('configuration', 'foundation/foundation/configuration/configuration.xml','configuration','config-foundation');

--
-- PIM Listener
--
ALTER TABLE fnbl_pim_listener_registry ADD COLUMN push_notes char(1);