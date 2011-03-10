--
-- Updating public mail servers
--
--
-- In the following is assumed that table fnbl_email_mailserver already contains records for GMail and Hotmail mail servers
-- (eg: GMail and Hotmail mail servers are inserted by defualt by the scripts at https://wiki.objectweb.org/sync4j/Wiki.jsp?page=Funambol60onMysql)
--
INSERT INTO fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol, out_server, out_port, out_auth, in_server, in_port, sslin, sslout, inbox_name, inbox_active, outbox_name, outbox_active, sent_name, sent_active, drafts_name, drafts_active, trash_name, trash_active, soft_delete)
    SELECT max(mailserver_id) + 1, 'y', 'AOL', 'AOL', 'imap', 'smtp.aol.com', 587, 'y', 'imap.aol.com', 143, 'n', 'n', 'INBOX', 'y', 'Outbox', 'y', 'Sent Items', 'n', 'Drafts', 'n', 'Trash', 'n', 'n' FROM fnbl_email_mailserver;
INSERT INTO fnbl_email_mailserver (mailserver_id, server_public, server_type, description, protocol, out_server, out_port, out_auth, in_server, in_port, sslin, sslout, inbox_name, inbox_active, outbox_name, outbox_active, sent_name, sent_active, drafts_name, drafts_active, trash_name, trash_active, soft_delete)
    SELECT max(mailserver_id) + 1, 'y', 'GMail', 'GoogleMail', 'imap', 'smtp.googlemail.com', 465, 'y', 'imap.googlemail.com', 993, 'y', 'y', 'INBOX', 'y','Outbox', 'y', '[Google Mail]/Sent Mail', 'n', '[Google Mail]/Drafts', 'n', '[Google Mail]/Trash', 'n', 'n' FROM fnbl_email_mailserver
UPDATE fnbl_id SET counter = (SELECT max(mailserver_id) + 1 FROM fnbl_email_mailserver) WHERE idspace = 'email.mailserverid';

UPDATE fnbl_email_mailserver SET server_public = 'y', server_type = 'GMail', description = 'GMail', protocol = 'imap', out_server = 'smtp.gmail.com', out_port = 465, out_auth = 'y', in_server = 'imap.gmail.com', in_port = 993, sslin = 'y', sslout = 'y', inbox_name = 'INBOX', inbox_active = 'y', outbox_name = 'Outbox', outbox_active = 'y', sent_name = '[Gmail]/Sent Mail', sent_active = 'n', drafts_name = '[Gmail]/Drafts', drafts_active = 'n', trash_name = '[Gmail]/Trash', trash_active = 'n', soft_delete = 'n'
    WHERE mailserver_id = '1';
UPDATE fnbl_email_mailserver SET server_public = 'y', server_type = 'Hotmail', description = 'Hotmail', protocol = 'pop3', out_server = 'smtp.live.com', out_port = 25, out_auth = 'y', in_server = 'pop3.live.com', in_port = 995, sslin = 'y', sslout = 'n', inbox_name = 'Inbox', inbox_active = 'y', outbox_name = 'Outbox', outbox_active = 'y', sent_name = 'Sent Items', sent_active = 'n', drafts_name = 'Drafts', drafts_active = 'n', trash_name = 'Trash', trash_active = 'n', soft_delete = 'n'
    WHERE mailserver_id = '3';
