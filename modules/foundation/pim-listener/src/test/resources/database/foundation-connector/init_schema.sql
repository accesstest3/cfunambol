--
-- Initialization data for the Foundation module
--
-- @version $Id: init_schema.sql,v 1.1 2008-04-29 07:41:00 piter_may Exp $
--

--
-- Module structure registration
--
delete from fnbl_module where id='foundation';
insert into fnbl_module (id, name, description)
values('foundation','foundation','Foundation');

delete from fnbl_connector where id='foundation';
insert into fnbl_connector(id, name, description)
values('foundation','FunambolFoundationConnector','Funambol Foundation Connector');

--
-- SyncSource Types
--
delete from fnbl_sync_source_type where id='contact-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('contact-foundation','PIM Contact SyncSource','com.funambol.foundation.engine.source.PIMContactSyncSource','com.funambol.foundation.admin.PIMContactSyncSourceConfigPanel');

delete from fnbl_sync_source_type where id='calendar-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('calendar-foundation','PIM Calendar SyncSource','com.funambol.foundation.engine.source.PIMCalendarSyncSource','com.funambol.foundation.admin.PIMCalendarSyncSourceConfigPanel');

delete from fnbl_sync_source_type where id='fs-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('fs-foundation','FileSystem SyncSource','com.funambol.foundation.engine.source.FileSystemSyncSource','com.funambol.foundation.admin.FileSystemSyncSourceConfigPanel');

delete from fnbl_sync_source_type where id='note-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('note-foundation','Notes SyncSource','com.funambol.foundation.engine.source.PIMNoteSyncSource','com.funambol.foundation.admin.PIMNoteSyncSourceConfigPanel');

delete from fnbl_sync_source_type where id='sif-fs-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('sif-fs-foundation','SIF SyncSource','com.funambol.foundation.engine.source.SIFSyncSource','com.funambol.foundation.admin.SIFSyncSourceConfigPanel');

delete from fnbl_sync_source_type where id='config-foundation';
insert into fnbl_sync_source_type(id, description, class, admin_class) 
values ('config-foundation','Configuration SyncSource','com.funambol.foundation.engine.source.ConfigSyncSource',null);

--
-- Connectors
--
delete from fnbl_connector_source_type where connector='foundation' and sourcetype='contact-foundation';
insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','contact-foundation');

delete from fnbl_connector_source_type where connector='foundation' and sourcetype='calendar-foundation';
insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','calendar-foundation');

delete from fnbl_connector_source_type where connector='foundation' and sourcetype='fs-foundation';
insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','fs-foundation');

delete from fnbl_connector_source_type where connector='foundation' and sourcetype='sif-fs-foundation';
insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','sif-fs-foundation');

delete from fnbl_connector_source_type where connector='foundation' and sourcetype='note-foundation';
insert into fnbl_connector_source_type(connector, sourcetype)
values('foundation','note-foundation');


--
-- Module - Connector
--
delete from fnbl_module_connector where module='foundation' and connector='foundation';
insert into fnbl_module_connector(module, connector)
values('foundation','foundation');

--
-- SyncSources
--
delete from fnbl_client_mapping 
where (sync_source='briefcase'  or sync_source='card' or sync_source='note' 
or sync_source='snote' or sync_source='scard' or sync_source='cal' 
or sync_source='event' or sync_source='task' or sync_source='scal' 
or sync_source='stask' or sync_source='sifnote' or sync_source='plainnote' 
or sync_source='configuration');

delete from fnbl_last_sync 
where (sync_source='briefcase'  or sync_source='card' or sync_source='note'
 or sync_source='snote' or sync_source='scard' or sync_source='cal' 
or sync_source='event' or sync_source='task' or sync_source='scal' 
or sync_source='stask' or sync_source='sifnote' or sync_source='plainnote'
 or sync_source='configuration');

delete from fnbl_sync_source where uri='briefcase';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('briefcase', 'foundation/foundation/fs-foundation/BriefcaseSource.xml','briefcase','fs-foundation');

delete from fnbl_sync_source where uri='note';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('note', 'foundation/foundation/fs-foundation/NoteSource.xml','note','fs-foundation');

delete from fnbl_sync_source where uri='card';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('card', 'foundation/foundation/contact-foundation/VCardSource.xml','card','contact-foundation');

delete from fnbl_sync_source where uri='scard';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('scard', 'foundation/foundation/contact-foundation/SIFContactSource.xml','scard','contact-foundation');

delete from fnbl_sync_source where uri='cal';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('cal', 'foundation/foundation/calendar-foundation/VCalendarSource.xml','cal','calendar-foundation');

delete from fnbl_sync_source where uri='event';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('event', 'foundation/foundation/calendar-foundation/VEventSource.xml','event','calendar-foundation');  
  
delete from fnbl_sync_source where uri='task';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('task', 'foundation/foundation/calendar-foundation/VTodoSource.xml','task','calendar-foundation'); 

delete from fnbl_sync_source where uri='scal';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('scal', 'foundation/foundation/calendar-foundation/SIFEventSource.xml','scal','calendar-foundation');

delete from fnbl_sync_source where uri='stask';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('stask', 'foundation/foundation/calendar-foundation/SIFTaskSource.xml','stask','calendar-foundation');

delete from fnbl_sync_source where uri='sifnote';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('sifnote', 'foundation/foundation/note-foundation/SIFNoteSource.xml','sifnote','note-foundation');

delete from fnbl_sync_source where uri='plainnote';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('plainnote', 'foundation/foundation/note-foundation/PlainTextNoteSource.xml','plainnote','note-foundation');

delete from fnbl_sync_source where uri='snote';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('snote', 'foundation/foundation/sif-fs-foundation/SIFNoteSource.xml','snote','sif-fs-foundation');

delete from fnbl_sync_source where uri='configuration';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('configuration', 'foundation/foundation/configuration/configuration.xml','configuration','config-foundation');


--
-- ID Generator
--
delete from fnbl_id where idspace='pim.id';
insert into fnbl_id (idspace, counter) values('pim.id',0);
