--
-- Initialization data for the Email Connector module
--
-- @version $Id: init_schema.sql,v 1.1 2008-03-25 11:29:04 gbmiglia Exp $ 
--

--
-- Source registration
--

delete from fnbl_sync_source_type where id='email-ss';
insert into fnbl_sync_source_type(id, description, class, admin_class) values('email-ss','Email SyncSource','com.funambol.email.engine.source.EmailSyncSource','com.funambol.email.admin.EmailSyncSourceConfigPanel');

delete from fnbl_module where id='email';
insert into fnbl_module (id, name, description) values('email','email','email');

delete from fnbl_connector where id='email';
insert into fnbl_connector(id, name, description, admin_class) values('email','FunambolEmailConnector','Funambol Email Connector','com.funambol.email.admin.ConsolePanel');

delete from fnbl_connector_source_type where connector='email' and sourcetype='email-ss';
insert into fnbl_connector_source_type(connector, sourcetype) values('email','email-ss');

delete from fnbl_module_connector where module='email' and connector='email';
insert into fnbl_module_connector(module, connector) values('email','email');

--
--  SyncSource
--

delete from fnbl_client_mapping where sync_source='mail';

delete from fnbl_last_sync where sync_source='mail';

delete from fnbl_sync_source where uri='mail';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('mail', 'email/email/email-ss/mail.xml','mail','email-ss');


--
-- public mail server
--

insert into fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol,
  out_server, out_port, out_auth, in_server, in_port, sslin, sslout,
  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active,
  drafts_name, drafts_active, trash_name, trash_active, soft_delete)
values ('1', 'y', 'GMail', 'Gmail', 'imap', 'smtp.gmail.com', 465, 'y', 'imap.gmail.com', 993,
        'y', 'y',
        'INBOX', 'y', 'Outbox', 'y', '[Gmail]/Sent Mail', 'n', '[Gmail]/Drafts', 'n', '[Gmail]/Trash', 'n', 'n');

insert into fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol,
  out_server, out_port, out_auth, in_server, in_port, sslin, sslout,
  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active,
  drafts_name, drafts_active, trash_name, trash_active, soft_delete)
values ('2', 'y', 'GMail', 'Google Mail', 'imap', 'smtp.googlemail.com', 465, 'y', 'imap.googlemail.com', 993,
        'y', 'y',
        'INBOX', 'y', 'Outbox', 'y', '[Google Mail]/Sent Mail', 'n', '[Google Mail]/Drafts', 'n', '[Google Mail]/Trash', 'n', 'n');

insert into fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol, 
  out_server, out_port, out_auth, in_server, in_port, sslin, sslout,  
  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active,
  drafts_name, drafts_active, trash_name, trash_active, soft_delete)
values ('3', 'y', 'Other', 'Yahoo', 'pop3', 'smtp.mail.yahoo.com', 465, 'y', 'pop.mail.yahoo.com', 995, 
        'y', 'y',
        'Inbox', 'y', 'Outbox', 'y', 'Sent', 'n', 'Drafts', 'n', 'Trash', 'n', 'n');

insert into fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol, 
  out_server, out_port, out_auth, in_server, in_port, sslin, sslout, 
  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active,
  drafts_name, drafts_active, trash_name, trash_active, soft_delete)
values ('4', 'y', 'AOL', 'AOL', 'imap', 'smtp.aol.com', 587, 'y', 'imap.aol.com', 143, 
        'n', 'n',
        'INBOX', 'y', 'Outbox', 'y', 'Sent Items', 'n', 'Drafts', 'n', 'Trash', 'n', 'n');

insert into fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol, 
  out_server, out_port, out_auth, in_server, in_port, sslin, sslout, 
  inbox_name,inbox_active, outbox_name,outbox_active, sent_name, sent_active,
  drafts_name, drafts_active, trash_name, trash_active, soft_delete)
values ('5', 'y', 'Hotmail', 'Hotmail', 'pop3', 'smtp.live.com', 587, 'y', 'pop3.live.com', 995, 
        'y', 'n',
        'Inbox', 'y', 'Outbox', 'y', 'Sent Items', 'n', 'Drafts', 'n', 'Trash', 'n', 'n');
        
--
-- id for sent emails
--

delete from fnbl_id where idspace='email.cacheid';
delete from fnbl_id where idspace='email.sentid';
delete from fnbl_id where idspace='email.folderid';
delete from fnbl_id where idspace='email.mailserverid';
insert into fnbl_id (idspace, counter) values('email.cacheid',0);
insert into fnbl_id (idspace, counter) values('email.sentid',0);
insert into fnbl_id (idspace, counter) values('email.folderid',0);
insert into fnbl_id (idspace, counter) values('email.mailserverid',100);
