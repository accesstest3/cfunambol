/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.server.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.ConfigItem;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.*;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.store.CapabilitiesPersistentStore;
import com.funambol.server.store.ConfigPersistentStore;

/**
 * This is an implementation of DeviceInventory interface that stores the
 * capabilities on DB.
 *
 * @see com.funambol.framework.server.inventory.DeviceInventory
 *
 * @version $Id: PSDeviceInventory.java,v 1.2 2008-05-22 12:39:16 nichele Exp $
 */
public class PSDeviceInventory
extends DeviceInventoryImpl implements LazyInitBean {

    // ------------------------------------------------------------ Private data
    private CapabilitiesPersistentStore capabilitiesPersistentStore = null;

    /**
     * Constructor without parameters
     */
    public PSDeviceInventory() {
    }

    /**
     * Returns the capabilitiesPersistentStore
     * @return the capabilitiesPersistentStore
     */
    public CapabilitiesPersistentStore getCapabilitiesPersistentStore() {
        return capabilitiesPersistentStore;
    }

    /**
     * Sets the capabilitiesPersistentStore
     * @param capabilitiesPersistentStore the capabilitiesPersistentStore to set
     */
    public void setCapabilitiesPersistentStore(
            CapabilitiesPersistentStore capabilitiesPersistentStore) {
        this.capabilitiesPersistentStore = capabilitiesPersistentStore;
    }

    /**
     * Used to store data about the device configuration
     */
    private ConfigPersistentStore configPersistentStore   = null;

    /**
     * Returns the ConfigPersistentStore
     * @return the ConfigPersistentStore
     */
    public ConfigPersistentStore getConfigPersistentStore() {
        return configPersistentStore;
    }

    /**
     * Sets the ConfigPersistentStore
     * @param configPersistentStore
     */
    public void setConfigPersistentStore(ConfigPersistentStore configPersistentStore) {
        this.configPersistentStore = configPersistentStore;
    }

    /**
     * Initialization.
     *
     * @throws BeanInitializationException in case of inittialization errors
     */
    @Override
    public void init() throws BeanInitializationException {

        try {

            super.init();

            if (capabilitiesPersistentStore instanceof LazyInitBean) {
                ((LazyInitBean)capabilitiesPersistentStore).init();
            }

            if (configPersistentStore instanceof LazyInitBean) {
                ((LazyInitBean)configPersistentStore).init();
            }

            //
            // Prepares the configuration map.
            //
            HashMap props = new HashMap(1);
            props.put(CONFIG_JNDI_DATA_SOURCE_NAME, jndiDataSourceName);

            capabilitiesPersistentStore.configure(props);
            configPersistentStore.configure(props);

        } catch (PersistentStoreException e) {
            throw new BeanInitializationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Searches the capabilities identified:
     * - by id
     * or, if the id is null:
     * - by the set: Model, Manufacturer, Firmware version, Software version
     *              (if all these properties are not empty. Otherwise the method has to return false)
     * and fills the given object with the capabilities in the inventory.
     * Returns true if the capabilities were found, false otherwise.
     *
     * @param caps Capabilities to get
     * @return true if capabilities were found, false otherwise
     * @throws DeviceInventoryException
     */
    @Override
    public boolean getCapabilities(Capabilities caps)
            throws DeviceInventoryException
    {
        boolean found = false;
        try {
            found = capabilitiesPersistentStore.read(caps);
        } catch (NotFoundException ex) {
            return false;
        } catch (Exception ex) {
            throw new DeviceInventoryException("Error getting the capabilities " +
                                                caps + " (" + ex.getMessage() + ")", ex);
        }

        return found;
    }

    /**
     * Add or update the given capabilities to the inventory.
     * If caps.getId() is null, the capabilities have to be added to the inventory.
     * If caps.getId() isn't null but in the inventory there aren't
     * the given capabilities an add will be performed.
     * If caps.getId() isn't null and in the inventory there are the given
     * capabilities an update will be performed.
     * If the capabilities are added and the concrete implementation creates
     * a new id for the capabilities, that id is caps.setId() must be called in order to update it.
     *
     * @param caps capabilities object to be saved
     * @throws DeviceInventoryException
     */
    @Override
    public void setCapabilities(Capabilities caps)
        throws DeviceInventoryException {
        try {
            capabilitiesPersistentStore.store(caps);
        } catch (Exception ex) {
            throw new DeviceInventoryException("Error setting the capabilities " +
                                                caps + " (" + ex.getMessage() + ")", ex);
        }
    }

    /**
     * Retrieves the capabilities that match the given clause.
     *
     * @param clause the clause capabilities to match
     * @return array of Cpabilities that match the given clause
     * @throws DeviceInventoryException
     */
    @Override
    public Capabilities[] queryCapabilities(Clause clause)
        throws DeviceInventoryException {

        try {
            return (Capabilities[])capabilitiesPersistentStore.
                read(clause);
        } catch (Exception ex) {
            throw new DeviceInventoryException("Error querying the capabilities " +
                                               "(" + ex.getMessage() + ")", ex);
        }
    }

    /**
     * Returns the number of the capabilities records in the repository that
     * match the given clause filter.
     * If clause is null or an instance of AllClause, count all records.
     *
     * @param clause the clause capabilities to match
     * @return number of capabilities that match the given clause
     * @throws DeviceInventoryException
     */
    @Override
    public int countCapabilities(Clause clause)
            throws DeviceInventoryException
    {
        try {
            return capabilitiesPersistentStore.count(clause);
        }  catch (Exception ex) {
            throw new DeviceInventoryException("Error counting the capabilities " +
                                               "(" + ex.getMessage() + ")", ex);
        }

    }

    /**
     * Removes the given capabilities
     * @param capabilities the capabilities to remove
     * @throws DeviceInventoryException
     */
    @Override
    public void removeCapabilities(Capabilities capabilities)
        throws DeviceInventoryException {
        if (capabilities == null) {
            return;
        }
        try {
            capabilitiesPersistentStore.delete(capabilities);
        } catch (PersistentStoreException ex) {
            throw new DeviceInventoryException("Error removing capabilities (" +
                                                ex.getMessage() + ")", ex);
        }
    }
    
    /**
     * Searches the config identified by principal ID and the uri, it fills
     * the given object with the info in the inventory.
     *
     * @param config the object to be filled with informations from repository
     *        (nodeUri must be set)
     * @return true if the config was found, false otherwise
     * @throws DeviceInventoryException
     */
    @Override
    public boolean getConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        try {
            boolean found = getConfigPersistentStore().read(config);
            return found;
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            throw new DeviceInventoryException("Error getting " + config, e);
        }
    }

    /**
     * Searches the configs for the given principal
     *
     * @param principal the principal
     * @return true if the config was found, false otherwise
     * @throws DeviceInventoryException if an error occurs
     */
    @Override
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal)
    throws DeviceInventoryException {

        return getConfigItems(principal, null);
    }

    /**
     * Updates the given config.
     *
     * @param config Config to be updated
     * @throws DeviceInventoryException
     */
    @Override
    public void setConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        try {
            getConfigPersistentStore().store(config);
        }  catch (Exception e) {
            throw new DeviceInventoryException("Error saving the config " +
                                                config, e);
        }
    }

    /**
     * Removes the given config and his capabilities
     * @param config the config to remove
     * @return true if the item has been found and deleted
     * @throws DeviceInventoryException
     */
    @Override
    public boolean removeConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        if (config == null) {
            return false;
        }
        try {
            return getConfigPersistentStore().delete(config);
        } catch (PersistentStoreException e) {
            throw new DeviceInventoryException("Error removing config:" +
                                               e.getMessage(), e);
        }
    }

    /**
     * Removes all the configs for the given principal
     * @param principal the principal
     * @throws DeviceInventoryException if an error occurs
     */
    @Override
    public int removeConfigItems(Sync4jPrincipal principal)
    throws DeviceInventoryException {
        
        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }
        
        try {
            return getConfigPersistentStore().removeConfigItems(principal);
        } catch (PersistentStoreException e) {
            throw new DeviceInventoryException("Error removing config for principal " +
                                               principal, e);
        }

    }
    
    /**
     * Reads the configurations for the given principal that satisfy the given clause 
     *
     * @param principal principal
     * @param clause 
     * @return a list with all config items for the given principal that satisfy the given clause
     * @throws DeviceInventoryException if an error occurs
     */        
    @Override
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal, Clause clause)
    throws DeviceInventoryException {
        
        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }
        List<ConfigItem> configs = new ArrayList<ConfigItem>();

        try {

            ConfigItem[] items = (ConfigItem[])
                getConfigPersistentStore().read(principal, clause);

            //
            // We don't return Arrays.asList(items) since the list obtained in this
            // way doesn't implement all the List methods
            //
            configs.addAll(Arrays.asList(items));
            return configs;

        } catch (Exception e) {
            throw new DeviceInventoryException("Error getting the config items for principal " +
                                                principal, e);
        }      
    }    
    
}
