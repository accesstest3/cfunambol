--
-- This script contains the ddl to drop the engine database.
--
-- @version $Id: drop_engine.ddl,v 1.1 2008-07-01 21:54:11 nichele Exp $
--

-- Deletion of fnbl_principal is necessary to clean each table which references 
-- fnbl_principal through foreing key constraints.  
delete from fnbl_principal;                      
            
drop table fnbl_client_mapping          if exists;
drop table fnbl_last_sync               if exists;
drop table fnbl_sync_source             if exists;

-- If a table references fnbl_principal by a foreing key constraint, the "drop table"
-- command fails and the ds-server database re-creation stops. Therefore a "drop
-- table cascade" command has to be executed instead.
drop table fnbl_principal               if exists cascade;

drop table fnbl_user_role               if exists;
drop table fnbl_user                    if exists;
drop table fnbl_device                  if exists;
drop table fnbl_id                      if exists;
drop table fnbl_connector_source_type   if exists;
drop table fnbl_module_connector        if exists;
drop table fnbl_sync_source_type        if exists;
drop table fnbl_module                  if exists;
drop table fnbl_connector               if exists;
drop table fnbl_role                    if exists;
drop table fnbl_ds_cttype_rx            if exists;
drop table fnbl_ds_cttype_tx            if exists;
drop table fnbl_ds_ctcap_prop_param     if exists;
drop table fnbl_ds_ctcap_prop           if exists;
drop table fnbl_ds_ctcap                if exists;
drop table fnbl_ds_filter_rx            if exists;
drop table fnbl_ds_filter_cap           if exists;
drop table fnbl_ds_mem                  if exists;
drop table fnbl_device_ext              if exists;
drop table fnbl_device_datastore        if exists;
drop table fnbl_device_caps             if exists;
drop table fnbl_device_config           if exists;
drop table fnbl_pending_notification    if exists;
