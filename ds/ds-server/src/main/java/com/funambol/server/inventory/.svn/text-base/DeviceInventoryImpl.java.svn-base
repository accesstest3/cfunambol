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

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.*;
import com.funambol.framework.server.inventory.*;
import com.funambol.framework.server.store.*;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.beans.BeanInitializationException;

import com.funambol.server.store.DevicePersistentStore;


/**
 * This the implementation open source of DeviceInventory interface.
 * @see DeviceInventory
 *
 * @version $Id: DeviceInventoryImpl.java,v 1.2 2008-05-22 12:38:16 nichele Exp $
 */
public class DeviceInventoryImpl implements DeviceInventory, LazyInitBean {

    // --------------------------------------------------------------- Constants
    public static final String CONFIG_JNDI_DATA_SOURCE_NAME = "jndi-data-source-name";

    // ------------------------------------------------------------ Private data
    protected DevicePersistentStore devicePersistentStore   = null;

    protected DataSource datasource = null;

    /**
     * Constructor without parameters
     */
    public DeviceInventoryImpl() {
    }

    /**
     * The JNDI name of the datasource to be used
     * @deprecated Since v7, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */
    protected String jndiDataSourceName = null;

    /**
     * @deprecated Since v7, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */    
    public String getJndiDataSourceName() {
        return this.jndiDataSourceName;
    }

    /**
     * @deprecated Since v7, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */        
    public void setJndiDataSourceName(String jndiDataSourceName)
    throws PersistentStoreException {
        this.jndiDataSourceName = jndiDataSourceName;
    }

    /**
     * Returns the devicePersistentStore
     * @return the devicePersistentStore
     */
    public DevicePersistentStore getDevicePersistentStore() {
        return devicePersistentStore;
    }

    /**
     * Sets the devicePersistentStore
     * @param the devicePersistentStore
     */
    public void setDevicePersistentStore(DevicePersistentStore devicePersistentStore) {
        this.devicePersistentStore = devicePersistentStore;
    }


    /**
     * Initialization.
     *
     * @throws BeanInitializationException in case of inittialization errors
     */
    public void init() throws BeanInitializationException {

        try {
            if (devicePersistentStore instanceof LazyInitBean) {
                ((LazyInitBean)devicePersistentStore).init();
            }

            //
            // Prepares the configuration map.
            //
            HashMap props = new HashMap(1);
            props.put(CONFIG_JNDI_DATA_SOURCE_NAME, jndiDataSourceName);

            devicePersistentStore.configure(props);

        } catch (PersistentStoreException e) {
            throw new BeanInitializationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * This implementation return always <code>false</code>
     * @return false
     * @throws DeviceInventoryException
     */
    public boolean getCapabilities(Capabilities caps)
    throws DeviceInventoryException {
        return false;
    }

    /**
     * The open source implementation does nothing
     * @throws DeviceInventoryException
     */
    public void setCapabilities(Capabilities caps)
    throws DeviceInventoryException {
        return;
    }

    /**
     * The open source implementation does nothing
     * @param clause the clause capabilities to match
     * @return an empty array
     * @throws DeviceInventoryException
     */
    public Capabilities[] queryCapabilities(Clause clause)
    throws DeviceInventoryException {
        return new Capabilities[0];
    }

    /* The open source implementation does nothing.
     * @param clause the clause capabilities to match
     * @return always 0
     * @throws DeviceInventoryException
     */
    public int countCapabilities(Clause clause)
    throws DeviceInventoryException {
        return 0;
    }

    /**
     * Update the identifier capabilities of the given device with the
     * specify identifier.
     *
     * @param deviceId the identifier of the device
     * @param capsId   the identifier of the capabilities
     *
     * @throws DeviceInventoryException
     */
    public void setDeviceIdCaps(String deviceId, Long capsId)
    throws DeviceInventoryException {
        try {
            devicePersistentStore.store(deviceId, capsId);
        }  catch (Exception e) {
            throw new DeviceInventoryException("Error setting the idCaps for '" +
                                                deviceId + "' (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Searches the device identified by device.getDeviceId() and fills
     * the given object with the info in the inventory.
     *
     * @param device the object to be filled with informations from repository
     *        ( deviceId must be set)
     * @return true if the device was found, false otherwise
     * @throws DeviceInventoryException
     */
    public boolean getDevice(Sync4jDevice device)
    throws DeviceInventoryException {
        try {
            return devicePersistentStore.read(device);
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            throw new DeviceInventoryException("Error getting the device " +
                                                device + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Searches the device identified by device.getDeviceId() and fills the
     * given object with the info in the inventory specifying whether device
     * capabilities should be retrieved as well.
     *
     * @param device the object to be filled with informations from repository
     *               (deviceId must be set)
     * @param caps specifys whether device capabilities should be retrieved or not
     * @return true if the device was found, false otherwise
     * @throws DeviceInventoryException
     */
    public boolean getDevice(Sync4jDevice device, boolean caps)
    throws DeviceInventoryException {
        boolean found = false;
        try {
            found = devicePersistentStore.read(device);
            if (found && caps) {
                getCapabilities(device.getCapabilities());
            }
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            throw new DeviceInventoryException("Error getting the device " +
                                                device + " (" + e.getMessage() + ")", e);
        }

        return found;
    }

    /**
     * Updates the given device. The capabilities of the device won't chance.
     *
     * @param device Device to be updated
     * @throws DeviceInventoryException
     */
    public void setDevice(Sync4jDevice device)
    throws DeviceInventoryException {
        try {

            if (device.getServerPassword() == null) {
                device.setServerPassword(getDefaultServerPassword());
            }
            devicePersistentStore.store(device);
        }  catch (Exception e) {
            throw new DeviceInventoryException("Error saving the device " +
                                                device + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Retrieves the devices that match the given clause.
     *
     * @param clause The additional where clause
     * @return Sync4jDevice[] devices found in DB
     * @throws DeviceInventoryException
     */
    public Sync4jDevice[] queryDevices(Clause clause)
    throws DeviceInventoryException {

        try {
            return (Sync4jDevice[])devicePersistentStore.read(new Sync4jDevice(), clause);
        } catch (Exception e) {
            throw new DeviceInventoryException("Error querying the device " +
                                               " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Retrieves the devices that match the given clause specifying whether
     * device capabilities should be retrieved as well.
     *
     * @param clause The additional where clause
     * @param caps   Specifys whether device capabilities should be retrieved
     * @return Sync4jDevice[] devices found in DB
     * @throws DeviceInventoryException
     */
    public Sync4jDevice[] queryDevices(Clause clause, boolean caps)
    throws DeviceInventoryException {

        try {
            Sync4jDevice[] devices = (Sync4jDevice[])
                                     devicePersistentStore.read(new Sync4jDevice(), clause);

            if (caps) {
                //
                // We have to fill the devices with the capabilities
                //
                for (int i=0; i<devices.length; i++) {
                    getCapabilities(
                        devices[i].getCapabilities()
                    );
                }
            }

            return devices;
        } catch (Exception e) {
            throw new DeviceInventoryException("Error querying the device " +
                                               " (" + e.getMessage() + ")", e);
        }

    }

    /**
     * Returns the number of the device records in the repository that match
     * the given clause filter. If clause is null or an instance of AllClause,
     * count all records.
     *
     * @param clause The additional where clause
     * @return int number of devices
     * @throws DeviceInventoryException
     */
    public int countDevices(Clause clause)
    throws DeviceInventoryException {

        try {
            return devicePersistentStore.count(new Sync4jDevice(), clause);
        } catch (Exception e) {
            throw new DeviceInventoryException("Error querying the device " +
                                               " (" + e.getMessage() + ")", e);
        }

    }

    /**
     * Removes the given device and his capabilities
     * @param device the device to remove
     * @throws DeviceInventoryException
     */
    public void removeDevice(Sync4jDevice device)
    throws DeviceInventoryException {

        if (device == null) {
            return;
        }
        Capabilities caps = device.getCapabilities();

        removeCapabilities(caps);

        try {
            devicePersistentStore.delete(device);
        } catch (PersistentStoreException e) {
            throw new DeviceInventoryException("Error removing device:" +
                                               e.getMessage(), e);
        }
    }

    /**
     * The open source implementation does nothing
     * @param capabilities the capabilities to remove
     * @throws DeviceInventoryException
     */
    public void removeCapabilities(Capabilities capabilities)
    throws DeviceInventoryException {

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
    public boolean getConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        return false;
    }

    /**
     * Searches the configs identified by principal ID.
     *
     * @param principalID principal ID
     * @return true if the config was found, false otherwise
     * @throws DeviceInventoryException
     */
    public List<ConfigItem> getConfigItems(long principalID)
    throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Updates the given config.
     *
     * @param config Config to be updated
     * @throws DeviceInventoryException
     */
    public void setConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Removes the given config and his capabilities
     * @param config the config to remove
     * @return true if the item has been found and deleted
     * @throws DeviceInventoryException
     */
    public boolean removeConfigItem(ConfigItem config)
    throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Removes all the configs for the given principal
     * @param principalID
     * @throws DeviceInventoryException
     */
    public int removeConfigItems(long principalID)
    throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Searches the configs for the given principal
     *
     * @param principal the principal
     * @return true if the config was found, false otherwise
     * @throws DeviceInventoryException if an error occurs
     */
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal) throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Reads the configurations for the given principal that satisfy the given clause 
     *
     * @param principal principal
     * @param clause 
     * @return a list with all config items for the given principal that satisfy the given clause
     * @throws DeviceInventoryException if an error occurs
     */      
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal, Clause clause) throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Reads the configurations for the given principal that satisfy the given clause 
     *
     * @param principal principal
     * @return a list with all config items for the given principal that satisfy the given clause
     * @throws DeviceInventoryException if an error occurs
     */        
    public int removeConfigItems(Sync4jPrincipal principal) throws DeviceInventoryException {
        throw new UnsupportedOperationException("Not supported");
    }

    protected String defaultServerPassword =
        DeviceInventory.DEFAULT_SERVER_PASSWORD;

    /**
     * Retrieves the default server password
     *
     * @return the default server password
     * @throws com.funambol.framework.server.inventory.DeviceInventoryException
     */
    public String getDefaultServerPassword() throws DeviceInventoryException {
        return this.defaultServerPassword;
    }

    /**
     * Sets the default server password
     *
     * @param defaultServerPassword set the default server password
     * @throws com.funambol.framework.server.inventory.DeviceInventoryException
     */
    public void setDefaultServerPassword(String defaultServerPassword)
    throws DeviceInventoryException {
        this.defaultServerPassword = defaultServerPassword;
    }

    /**
     * Update the flag that indicates if the server caps has already been sent
     * to the client for the given device identifier.
     *
     * @param deviceId the identifier of the device
     * @param sentServerCaps has the server caps already been sent to the client?
     *
     * @throws DeviceInventoryException
     */
    public void setSentServerCaps(String deviceId, boolean sentServerCaps)
    throws DeviceInventoryException {
        try {
            devicePersistentStore.store(deviceId, sentServerCaps);
        }  catch (Exception e) {
            throw new DeviceInventoryException(
                "Error setting the flag about server caps for '" + deviceId +
                "' (" + e.getMessage() + ")", e);
        }
    }

}
