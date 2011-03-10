--
-- Drop of the Schema for the Email Connector module
--
-- @version $Id: drop_schema.sql,v 1.1 2008-03-25 11:28:56 gbmiglia Exp $
--

drop table if exists fnbl_email_cache         ;
drop table if exists fnbl_email_inbox         ;
drop table if exists fnbl_email_folder        ;
drop table if exists fnbl_email_sentpop       ;

drop table if exists fnbl_email_mailserver    ;
drop table if exists fnbl_email_push_registry ;
drop table if exists fnbl_email_account       ;
drop table if exists fnbl_email_enable_account;
