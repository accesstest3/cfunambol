--
-- Pim Listener migration
--

DROP INDEX ind_push_listener_registry;
ALTER TABLE fnbl_push_listener_registry DROP COLUMN id_push_listener;
ALTER TABLE fnbl_pim_listener_registry ALTER COLUMN username TYPE varchar(255);

--
-- Foundation migration
--
CREATE TABLE fnbl_pim_contact_photo (
    contact      bigint,
    type         varchar(64),
    photo        bytea,
    url          varchar(255),
    PRIMARY KEY (contact),
    FOREIGN KEY (contact) REFERENCES fnbl_pim_contact
                          ON DELETE CASCADE
);

ALTER TABLE fnbl_pim_contact ADD COLUMN photo_type smallint;

--
-- Email connector migration
--

CREATE TABLE fnbl_email_push_registry (
  id               bigint PRIMARY KEY,
  period           bigint,
  active           char(1),
  last_update      bigint,
  status           varchar(1),
  task_bean_file   varchar(255)
);

INSERT INTO fnbl_email_push_registry (id, period, active, status, last_update, task_bean_file)
    SELECT 
        account_id::text::bigint,
        refresh_time * 1000,
        UPPER(activation),
        'N',
        0,
        'com/funambol/email/inboxlistener/task/InboxListenerTask.xml'
    FROM fnbl_email_account;

INSERT INTO fnbl_id (idspace, counter, increment_by) (select 'email.registryid', max(id)+1, 100 from fnbl_email_push_registry);

ALTER TABLE fnbl_email_account    DROP COLUMN listener_id;
ALTER TABLE fnbl_email_account    DROP COLUMN activation;
ALTER TABLE fnbl_email_account    DROP COLUMN refresh_time;
ALTER TABLE fnbl_email_account    DROP COLUMN keystore_path;
ALTER TABLE fnbl_email_account    DROP COLUMN keystore_pwd;
ALTER TABLE fnbl_email_account    ADD COLUMN ms_mailboxname varchar(64);

ALTER TABLE fnbl_email_mailserver DROP COLUMN keystore_path;
ALTER TABLE fnbl_email_mailserver DROP COLUMN keystore_pwd;

ALTER TABLE fnbl_email_account ADD COLUMN account_id_tmp bigint;
UPDATE fnbl_email_account SET account_id_tmp = account_id::int8;
ALTER TABLE fnbl_email_account DROP COLUMN account_id;
ALTER TABLE fnbl_email_account RENAME COLUMN account_id_tmp TO account_id;
ALTER TABLE fnbl_email_account ADD CONSTRAINT pk_user_account primary key (account_id, username);

ALTER TABLE fnbl_email_cache   ALTER COLUMN principal type bigint;
ALTER TABLE fnbl_email_cache   ALTER COLUMN messageid type varchar(700);
ALTER TABLE fnbl_email_folder  ALTER COLUMN principal type bigint;
ALTER TABLE fnbl_email_sentpop ALTER COLUMN principal type bigint;
ALTER TABLE fnbl_email_sentpop ALTER COLUMN messageid type varchar(700);
ALTER TABLE fnbl_email_inbox   ALTER COLUMN messageid type varchar(700);

ALTER TABLE fnbl_email_cache   ADD CONSTRAINT fk_principal_email_cache   FOREIGN KEY (principal) REFERENCES fnbl_principal (id) ON DELETE CASCADE;
ALTER TABLE fnbl_email_folder  ADD CONSTRAINT fk_principal_email_folder  FOREIGN KEY (principal) REFERENCES fnbl_principal (id) ON DELETE CASCADE;
ALTER TABLE fnbl_email_sentpop ADD CONSTRAINT fk_principal_email_sentpop FOREIGN KEY (principal) REFERENCES fnbl_principal (id) ON DELETE CASCADE;

--
-- SMTP Authentication support
--
ALTER TABLE fnbl_email_account ADD COLUMN out_login    varchar(50);
ALTER TABLE fnbl_email_account ADD COLUMN out_password varchar(50);