--
-- Script to create the core engine database.
--
-- @version $Id: create-core-schema.sql 34393 2010-05-04 16:45:30Z luigiafassina $
--

create table fnbl_sync_source (
  uri        varchar(128) not null,
  config     varchar(255) not null,
  name       varchar(200) not null,
  sourcetype varchar(128) not null,
  
  constraint pk_sync_source primary key (uri)
);