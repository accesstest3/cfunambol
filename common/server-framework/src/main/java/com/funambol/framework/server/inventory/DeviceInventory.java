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

package com.funambol.framework.server.inventory;

import java.util.List;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.*;
import com.funambol.framework.security.Sync4jPrincipal;

/**
 * The interface to store and read capabilities and devices.
 *
 * @version $Id: DeviceInventory.java,v 1.5 2008-05-22 13:55:12 nichele Exp $
 */
public interface DeviceInventory {

    public static final String DEFAULT_SERVER_PASSWORD = "fnbl";

    /**
     * Retrieves the default server password.
     *
     * @return the default server password
     * @throws DeviceInventoryException if an error occurs
     */
    public String getDefaultServerPassword() throws DeviceInventoryException;

    /**
     * Sets the default server password.
     *
     * @param defaultServerPassword set the default server password
     * @throws DeviceInventoryException if an error occurs
     */
    public void setDefaultServerPassword(String defaultServerPassword)
    throws DeviceInventoryException;

    /**
     * Searches the capabilities identified:
     *  -id
     *  or, if the id is null:
     *  - by the set:Model,Manufacturer,Firmware version,Software version(if 
     *    all these properties are not empty. Otherwise the method has to
     *    return false)
     * and fills the given object with the capabilities in the inventory
     * Returns true if the capabilities were found, false otherwise.
     *
     * @param caps Capabilities to get
     * @return true if capabilities were found, false otherwise
     * @throws DeviceInventoryException
     */
    public boolean getCapabilities(Capabilities caps) throws DeviceInventoryException;

    /**
     * Adds or updates the given capabilities to the inventory.
     * If caps.getId() is null, the capabilities have to be added to the inventory.
     * If caps.getId() isn't null but in the inventory there aren't the given
     * capabilities an add will be performed.
     * If caps.getId() isn't null and in the inventory there are the given
     * capabilities an update will be performed.
     * If the capabilities are added and the concrete implementation creates a 
     * new id for the capabilities, that id is caps.setId() must be called in
     * order to update it.
     *
     * @param caps capabilities object to be saved
     * @throws DeviceInventoryException
     */
    public void setCapabilities(Capabilities  caps) throws DeviceInventoryException;

    /**
     * Retrieves the capabilities that match the given clause filter.
     *
     * @param clause the clause capabilities to match
     * @return array of Cpabilities that match the given clause
     * @throws DeviceInventoryException
     */
    public Capabilities[] queryCapabilities(Clause clause) throws DeviceInventoryException;

    /**
     * Returns the number of the capabilities records in the repository that
     * match the given clause filter.
     * If clause is null or an instance of AllClause, count all records.
     *
     * @param clause the clause capabilities to match
     * @return number of capabilities that match the given clause
     * @throws DeviceInventoryException
     */
    public int countCapabilities(Clause clause) throws DeviceInventoryException;

    /**
     * Searches the device identified by device.getDeviceId() and fills the
     * given object with the info in the inventory
     *
     * @param device the object to be filled with informations from repository
     *               ( deviceId must be set)
     * @return true if the device was found, false otherwise
     * @throws DeviceInventoryException
     */
    public boolean getDevice(Sync4jDevice device) throws DeviceInventoryException;

    /**
     * Searches the device identified by device.getDeviceId() and fills the 
     * given object with the info in the inventory specifying whether device
     * capabilities should be retrieved as well.
     *
     * @param device the object to be filled with informations from repository ( deviceId must be set)
     * @param caps specifys whether device capabilities should be retrieved or not
     * @return true if the device was found, false otherwise
     * @throws DeviceInventoryException
     */
    public boolean getDevice(Sync4jDevice device, boolean caps) throws DeviceInventoryException;

    /**
     * Updates the given device. The capabilities of the device won't chance.
     *
     * @param device Device to be updated
     * @throws DeviceInventoryException
     */
    public void setDevice(Sync4jDevice device) throws DeviceInventoryException;

    /**
     * Updates the identifier capabilities of the given device with the
     * specified identifier.
     *
     * @param deviceId the identifier of the device
     * @param capsId   the identifier of the capabilities
     *
     * @throws DeviceInventoryException
     */
    public void setDeviceIdCaps(String deviceId, Long capsId)
    throws DeviceInventoryException;

    /**
     * Retrieves the devices that match the given clause.
     *
     * @param clause The additional where clause
     * @return Sync4jDevice[] devices found in DB
     * @throws DeviceInventoryException
     */
    public Sync4jDevice[] queryDevices(Clause clause) throws DeviceInventoryException;

    /**
     * Retrieves the devices that match the given clause specifying whether
     * device capabilities should be retrieved as well.
     *
     * @param clause The additional where clause
     * @param caps   Specifys whether device capabilities should be retrieved
     * @return Sync4jDevice[] devices found in DB
     * @throws DeviceInventoryException
     */
    public Sync4jDevice[] queryDevices(Clause clause, boolean caps) throws DeviceInventoryException;

    /**
     * Returns the number of the devices in the repository that match the given 
     * clause filter. If clause is null or an instance of AllClause, count all
     * records.
     *
     * @param clause The additional where clause
     * @return int number of devices
     * @throws DeviceInventoryException
     */
    public int countDevices(Clause clause) throws DeviceInventoryException;

    /**
     * Removes the given device and its capabilities.
     *
     * @param device the device to remove
     * @throws DeviceInventoryException
     */
    public void removeDevice(Sync4jDevice device) throws DeviceInventoryException;

    /**
     * Removes the given capabilities.
     *
     * @param capabilities the capabilities to remove
     * @throws DeviceInventoryException
     */
    public void removeCapabilities(Capabilities capabilities)
    throws DeviceInventoryException;
    
    /**
     * Searches the configuration identified by principal ID and fills the
     * given object with the info in the inventory.
     *
     * @param config the object to be filled with informations from repository
     * (principal ID must be set)
     * @return true if the config was found, false otherwise
     * @throws DeviceInventoryException if an error occurss
     */
    public boolean getConfigItem(ConfigItem config) throws DeviceInventoryException;

    /**
     * Sets the given config item.
     *
     * @param config the config item to set
     * @throws DeviceInventoryException if an error occurs
     */
    public void setConfigItem(ConfigItem config) throws DeviceInventoryException;

    /**
     * Removes the given config item.
     *
     * @param config the config to remove
     * @return true if the item has been found and deleted
     * @throws DeviceInventoryException if an error occurs
     */
    public boolean removeConfigItem(ConfigItem config) throws DeviceInventoryException;

    /**
     * Reads the configurations for the given principal.
     *
     * @param principal principal
     * @return a list with all config items for the given principal
     * @throws DeviceInventoryException if an error occurs
     */
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal)
    throws DeviceInventoryException;
    
    /**
     * Reads the configurations for the given principal that satisfy the given 
     * clause.
     *
     * @param principal principal
     * @param clause 
     * @return a list with all config items for the given principal that satisfy
     *         the given clause
     * @throws DeviceInventoryException if an error occurs
     */    
    public List<ConfigItem> getConfigItems(Sync4jPrincipal principal, Clause clause)
    throws DeviceInventoryException;

    /**
     * Removes all the config items for the given principal.
     *
     * @param principal the principal
     * @return the number of removed items
     * @throws DeviceInventoryException if an error occurs
     */
    public int removeConfigItems(Sync4jPrincipal principal)
    throws DeviceInventoryException;

    /**
     * Updates the flag that indicates if the server caps have already been sent
     * to the client for the given device identifier.
     *
     * @param deviceId the identifier of the device
     * @param sentServerCaps has the server caps already been sent to the client?
     *
     * @throws DeviceInventoryException
     */
    public void setSentServerCaps(String deviceId, boolean sentServerCaps)
    throws DeviceInventoryException;

}
