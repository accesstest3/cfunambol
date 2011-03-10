--
-- Creates PIM Listener tables
--
-- @version $Id: create_pimlistener.sql,v 1.1 2008-05-18 17:19:58 nichele Exp $
--

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