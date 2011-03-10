--
-- Initialization data for the PIM module
--
-- @version $Id: drop_schema.sql,v 1.1.1.1 2008-03-20 21:38:28 stefano_fornari Exp $
--

drop table fnbl_pim_contact_photo         if exists;
drop table fnbl_pim_contact_item          if exists;
drop table fnbl_pim_address               if exists;
drop table fnbl_pim_contact               if exists;
drop table fnbl_pim_calendar_exception    if exists;
drop table fnbl_pim_calendar              if exists;
drop table fnbl_pim_listener_registry     if exists;
drop table fnbl_push_listener_registry    if exists;
drop table fnbl_pim_note                  if exists;
drop table fnbl_file_data_object_property if exists;
drop table fnbl_file_data_object          if exists;
