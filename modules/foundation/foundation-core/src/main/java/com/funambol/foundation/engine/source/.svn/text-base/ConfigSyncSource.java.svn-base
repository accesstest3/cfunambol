/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.foundation.engine.source;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.ConfigManager;
import com.funambol.foundation.util.Def;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ConfigItem;

/**
 * SyncSource for ConfigItem synchronization
 * @version $Id: ConfigSyncSource.java,v 1.1.1.1 2008-03-20 21:38:33 stefano_fornari Exp $
 */
public class ConfigSyncSource 
        extends AbstractSyncSource
        implements Serializable, LazyInitBean {

    //-----------------------------------------------------------------Constants

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    public static final String contentType = "text/plain";

    //--------------------------------------------------------------Private data

    private transient ConfigManager manager;

    private String          username = null;
    private String          deviceID = null;
    private Sync4jPrincipal principal = null;

    //------------------------------------------------------------Public methods

    /**
     * LazyInitBean initialization
     * @throws com.funambol.framework.tools.beans.BeanInitializationException
     */
    public void init() throws BeanInitializationException {
    }
    
    /**
     * Begins the sync
     */
    public void beginSync(SyncContext context) throws SyncSourceException {
        try {

            if (log.isTraceEnabled()) {
                log.trace("ConfigSyncSource beginSync");
            }
            
            this.principal = context.getPrincipal();            
            this.deviceID  = principal.getDeviceId();
            this.username  = principal.getUsername();
            
            this.manager     = new ConfigManager(principal);

        } catch (Exception e){
            throw new SyncSourceException("Error starting sync for the principal " +
                    this.principal, e);
        }
    }

    /**
     * Ends the sync
     */
    public void endSync() throws SyncSourceException {
        if (log.isTraceEnabled()) {
            log.trace("ConfigSyncSource endSync");
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all config IDs.
     *
     * @return a SyncItemKey array
     */
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("ConfigSyncSource get All SyncItem Keys");
        }
        try {
            List<ConfigItem> configItems = manager.getAllItems();
            SyncItemKey[] keyList = new SyncItemKey[configItems.size()];
            for (int i = 0; i < configItems.size(); i++) {
                keyList[i] = new SyncItemKey((String) configItems.get(i).getNodeURI());
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving all item keys. ", e);
        }

    }

    /**
     * Adds a SyncItem object (representing a config item).
     *
     * @param syncItem the SyncItem representing the config item
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.NEW and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem addSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Adding item: " + syncItem);
        }

        try {

            String nodeURI = syncItem.getKey().getKeyAsString();

            String value = getContentFromSyncItem(syncItem);

            Timestamp ts = syncItem.getTimestamp();

            manager.setItem(nodeURI, value, ts);

            // Adds the contact, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                this                  , //syncSource
                nodeURI               , //key
                null                  , //mappedKey
                SyncItemState.NEW     , //state
                value.getBytes()      , //content
                null                  , //format
                contentType           , //type
                ts                      //timestamp
            );

            return newSyncItem;

        } catch (Exception e) {
            log.error("Error adding the item " + syncItem, e);
            throw new SyncSourceException("Error adding the item " + syncItem,
                                          e);
        }
    }

    /**
     * Updates a SyncItem object (representing a contact).
     *
     * @param syncItem the SyncItem representing the contact
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.UPDATED and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Updating item: " + syncItem);
        }

        try {

            String nodeURI = syncItem.getKey().getKeyAsString();

            String value = getContentFromSyncItem(syncItem);

            Timestamp ts = syncItem.getTimestamp();

            manager.setItem(nodeURI, value, ts);

            // Modifies the contact, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                this                    , //syncSource
                nodeURI                 , //nodeURI
                null                    , //mappedKey
                SyncItemState.UPDATED   , //state
                value.getBytes()        , //content
                null                    , //format
                contentType             , //type
                null                      //timestamp
            );

            return newSyncItem;

        } catch (Exception e) {
            log.error("Error updating the item " + syncItem, e);
            throw new SyncSourceException("Error updating the item " + syncItem,
                                          e);
        }
    }

    /**
     * Deletes the item with a given syncItemKey.
     *
     * @param syncItemKey
     * @param timestamp in case of a soft deletion, this will be the registered
     *                  moment of deletion; if a hard deletion is used, this
     *                  field is irrelevant and may also be null
     * @param softDelete it is true if the client requires a soft deletion
     * @throws SyncSourceException
     */
    @Override
    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp timestamp,
                               boolean softDelete)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Deleting item with key: " + syncItemKey);
        }

        try {
            manager.removeItem(syncItemKey.getKeyAsString(),
                               timestamp);
        } catch (EntityException e) {
            log.error("Error deleting item with key " + syncItemKey, e);
            throw new SyncSourceException("Error deleting item with key " + syncItemKey, e);
        }

    }

    /**
     * Retrieves the config and uses it to create a new SyncItem
     * which is the return value of this method
     */
    @Override
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Getting item with key " + syncItemKey);
        }
        
        String  nodeURI = syncItemKey.getKeyAsString();

        SyncItem syncItem = null;
        try {
            ConfigItem configItem = manager.getItem(nodeURI);
            //
            // We need to use the nodeURI since the configItem could be null
            //
            syncItem = createSyncItem(nodeURI, configItem, SyncItemState.NEW);
        } catch (EntityException e) {
            throw new SyncSourceException("Error seeking SyncItem with ID: " + nodeURI , e);
        }
        return syncItem;

    }

    /**
     * Makes an array of SyncItemKey objects representing all new config item
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws SyncSourceException if an error occurs
     */
    @Override
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Getting new ConfigItem keys");
        }
        try {
            List<ConfigItem> configItems = manager.getItems(SyncItemState.NEW, since, to);
            SyncItemKey[] keyList = new SyncItemKey[configItems.size()];
            for (int i = 0; i < configItems.size(); i++) {
                keyList[i] = new SyncItemKey((String) configItems.get(i).getNodeURI());
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving new item keys", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted contact
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws SyncSourceException if an error occurs
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Getting updated ConfigItem keys");
        }
        try {
            List<ConfigItem> configItems = manager.getItems(SyncItemState.UPDATED, since, to);
            SyncItemKey[] keyList = new SyncItemKey[configItems.size()];
            for (int i = 0; i < configItems.size(); i++) {
                keyList[i] = new SyncItemKey((String) configItems.get(i).getNodeURI());
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving updated item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted contact
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws SyncSourceException if an error occurs
     */
    @Override
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Getting deleted ConfigItem keys");
        }
        try {
            List<ConfigItem> configItems = manager.getItems(SyncItemState.DELETED, since, to);
            SyncItemKey[] keyList = new SyncItemKey[configItems.size()];
            for (int i = 0; i < configItems.size(); i++) {
                keyList[i] = new SyncItemKey((String) configItems.get(i).getNodeURI());
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving deleted item keys", e);
        }
    }

    /**
     * Not used yet. It just produces a log message.
     */
    public void setOperationStatus(String operation, int statusCode,
            SyncItemKey[] keys) {

        if (log.isTraceEnabled()) {
            StringBuilder message = new StringBuilder("Received status code '");
            message.append(statusCode).append("' for a '").append(operation)
                    .append("' command for the following items: ");

            for (int i = 0; i < keys.length; i++) {
                message.append("\n> ").append(keys[i].getKeyAsString());
            }

            log.trace(message.toString());
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing the ID(s) of the
     * twin(s) of a given config.
     *
     * @param syncItem the SyncItem representing the item whose twin(s) are
     *                 looked for
     * @throws SyncSourceException
     * @return possibly, just one or no key should be in the array, but it can't
     *         be ruled out a priori that several keys get returned by this
     *         method
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Searching twins of " + syncItem);
        }
        try {
            String nodeURI = syncItem.getKey().getKeyAsString();
            //
            // Two config item are twin if the node uri is equal and so we can have
            // just one twin
            //
            ConfigItem twin = manager.getItem(nodeURI);
            SyncItemKey[] keyList = null;
            if (twin != null) {
                keyList = new SyncItemKey[] {new SyncItemKey(twin.getNodeURI())};
            } else {
                keyList = new SyncItemKey[0];
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving twin item keys", e);
        }
    }

    //---------------------------------------------------------- Private methods

    /**
     * Create a new SyncItem from a ConfigItem. The target contentType and status
     * are passed as arguments.
     *
     * @param key the item key
     * @param item the config item
     * @param status the status
     * @return a newly created SyncItem object
     */
    private SyncItem createSyncItem(String key, ConfigItem item, char status) {

        SyncItem syncItem = new SyncItemImpl(this, key, status);

        syncItem.setType(contentType);
        if (item != null && item.getValue() != null) {
            syncItem.setContent(item.getValue().getBytes());
        }

        return syncItem;
    }


   /**
    * Extracts the content from a syncItem.
    *
    * @param syncItem
    * @return as a String object
    */
   private String getContentFromSyncItem(SyncItem syncItem) {
       byte[] itemContent = syncItem.getContent();
       String content = new String(itemContent == null ? new byte[0] : itemContent);
       if ("b64".equalsIgnoreCase(syncItem.getFormat())){
           // @todo decode and set ?
       }
       return content;
   }

}
