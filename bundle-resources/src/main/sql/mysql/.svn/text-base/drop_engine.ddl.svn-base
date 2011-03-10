--
-- This script contains the ddl to drop the engine database.
--
-- @version $Id: drop_engine.ddl,v 1.1 2008-07-01 21:54:11 nichele Exp $
--

-- Deletion of fnbl_principal is necessary to clean each table which references 
-- fnbl_principal through foreing key constraints.                                    
delete from fnbl_principal;

-- If a table references fnbl_principal by a foreing key constraint, the "drop table"
-- command fails and the ds-server database re-creation stops. Therefore foreign
-- key constraints need to be disabled before dropping fnbl_principal table.
SET FOREIGN_KEY_CHECKS = 0;                          
        
drop table if exists fnbl_client_mapping       ;
drop table if exists fnbl_last_sync            ;
drop table if exists fnbl_sync_source          ;
drop table if exists fnbl_principal            ;
drop table if exists fnbl_user_role            ;
drop table if exists fnbl_user                 ;
drop table if exists fnbl_device               ;
drop table if exists fnbl_id                   ;
drop table if exists fnbl_connector_source_type;
drop table if exists fnbl_module_connector     ;
drop table if exists fnbl_sync_source_type     ;
drop table if exists fnbl_module               ;
drop table if exists fnbl_connector            ;
drop table if exists fnbl_role                 ;
drop table if exists fnbl_ds_cttype_rx         ;
drop table if exists fnbl_ds_cttype_tx         ;
drop table if exists fnbl_ds_ctcap_prop_param  ;
drop table if exists fnbl_ds_ctcap_prop        ;
drop table if exists fnbl_ds_ctcap             ;
drop table if exists fnbl_ds_filter_rx         ;
drop table if exists fnbl_ds_filter_cap        ;
drop table if exists fnbl_ds_mem               ;
drop table if exists fnbl_device_ext           ;
drop table if exists fnbl_device_datastore     ;
drop table if exists fnbl_device_caps          ;
drop table if exists fnbl_device_config        ;
drop table if exists fnbl_pending_notification ;
