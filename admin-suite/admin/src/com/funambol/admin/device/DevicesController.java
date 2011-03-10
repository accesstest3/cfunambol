/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.device;

import java.text.MessageFormat;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import com.funambol.framework.core.CTCapV1;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.Ext;
import com.funambol.framework.core.VerDTD;
import com.funambol.framework.filter.*;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.Sync4jDevice;

import com.funambol.admin.AdminException;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.device.panels.CreateDevicePanel;
import com.funambol.admin.device.panels.SearchDevicePanel;
import com.funambol.admin.device.panels.devinf.CapabilitiesPanel;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Device Controller class, all the device process actions  like creation,
 * delete, search and save are managed here.
 * <p>All the actions on devices are then dispatched to the business delegate</p>
 *
 * @version $Id: DevicesController.java,v 1.10 2007-11-28 10:28:18 nichele Exp $
 */

public class DevicesController
implements Constants {

    /** constant for search query identifing device_id */
    private static String PARAM_ID = "id";

    // ------------------------------------------------------------ Private data
    private BusinessDelegate businessDelegate = null;

    // ------------------------------------------------------------ Constructors
    /**
     * Create a new deviceController.
     * @param businessDelegate BusinessDelegate the reference
     * to the application businessDelegate
     */
    public DevicesController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Starts a new device process, showing the creation panel.
     */
    public void startNewDeviceProcess() {
        CreateDevicePanel panel = (CreateDevicePanel)PanelManager.getPanel(Bundle.getMessage(Bundle.
            INSERT_DEVICE_PANEL_NAME));

        if (panel != null) {
            PanelManager.showPanel(Bundle.getMessage(Bundle.INSERT_DEVICE_PANEL_NAME));

        } else {
            // create new panel
            panel = new CreateDevicePanel();
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Starts the process of edit a device
     * @param deviceToEdit the device to edit
     * @throws AdminException if a error occurs during start the edit process
     */
    public void startEditDeviceProcess(Sync4jDevice deviceToEdit) throws AdminException {

        Log.debug("Start edit device process");
        
        //
        // Used to re-load the device with updated informations
        //
        Clause clause = getClauseForDeviceSearchById(deviceToEdit.getDeviceId());
        deviceToEdit = businessDelegate.getDevices(clause)[0];
        Capabilities caps = businessDelegate.getDeviceCapabilities(deviceToEdit.getDeviceId());
        deviceToEdit.setCapabilities(caps);

        CreateDevicePanel panel = (CreateDevicePanel)PanelManager.getPanel(Bundle.getMessage(Bundle.
            EDIT_DEVICE_PANEL_NAME));

        if (panel != null) {
            panel.loadDevice(deviceToEdit);
            PanelManager.showPanel(Bundle.getMessage(Bundle.EDIT_DEVICE_PANEL_NAME));
        } else {
            panel = new CreateDevicePanel();
            panel.loadDevice(deviceToEdit);
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Starts a new search device process, showing the device panel
     */
    public void startSearchDeviceProcess() {
        SearchDevicePanel panel = (SearchDevicePanel)PanelManager.getPanel(Bundle.getMessage(Bundle.
            SEARCH_DEVICE_PANEL_NAME));

        if (panel != null) {
            PanelManager.showPanel(Bundle.getMessage(Bundle.SEARCH_DEVICE_PANEL_NAME));
        } else {
            // create new panel
            panel = new SearchDevicePanel();
            panel.validate();
            PanelManager.addPanel(panel);

        }
    }

    /**
     * Delete the device, after showing a confirm message.
     * @param deviceToDelete Sync4jDevice the device to be deleted.
     * @throws AdminException
     * @return boolean true if the device is deleted false otherwise
     */
    public boolean deleteDevice(Sync4jDevice deviceToDelete) throws AdminException {

        boolean deviceDeleted = false;
        if (showConfirmDelete()) {
            businessDelegate.deleteDevice(deviceToDelete.getDeviceId());
            Log.info(Bundle.getMessage(Bundle.DEVICE_MESSAGE_DELETE_OK));
            deviceDeleted = true;
        }

        return deviceDeleted;

    }

    /**
     * Saves the specified device, giving it to the businessDelegate.
     * Also handles error messages, for example if the device already exists.
     * @param device Sync4jDevice the device
     * @throws AdminException
     * @return boolean true if the device is saved
     */
    public boolean saveNewDevice(Sync4jDevice device) throws AdminException {
        boolean deviceSaved = false;

        boolean deviceOk = checkDeviceValue(device);

        if (!deviceOk) {
            return deviceSaved;
        }

        // check if deviceId already exists
        Clause clause = getClauseForDeviceSearchById(device.getDeviceId());

        Sync4jDevice[] devices = null;
        devices = businessDelegate.getDevices(clause);

        if (devices.length > 0) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.DEVICE_MESSAGE_ID_EXISTS),
                    new String[] { device.getDeviceId() }
                )
            );
            DialogDisplayer.getDefault().notify(desc);

            return deviceSaved;
        }

        businessDelegate.addDevice(device);
        Log.info(Bundle.getMessage(Bundle.DEVICE_MESSAGE_INSERT_OK));
        deviceSaved = true;

        return deviceSaved;
    }

    /**
     * Starts process to show the device information
     */
    public void startDevInfProcess(Sync4jDevice device) throws AdminException {
        
        CapabilitiesPanel panel = (CapabilitiesPanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.CAPABILITIES_DETAILS_PANEL_NAME)
        );

        if (panel != null) {            
            PanelManager.removePanel(panel);
        }       
        
        Capabilities caps =
            businessDelegate.getDeviceCapabilities(device.getDeviceId());

        panel = new CapabilitiesPanel(device.getDeviceId());
        
        if (caps == null || -1 == caps.getId().intValue()) {
            panel.loadEmptyCaps();
        } else {
            panel.loadCaps(caps);
        }
        
        panel.validate();
        PanelManager.addPanel(panel);
    }
    
    /**
     * Saves the specified device, giving it to the businessDelegate.
     * Also handles error messages, for example if the device already exists.
     * @param device Sync4jDevice the device
     * @throws AdminException
     * @return boolean true if the device is saved
     */
    public void saveDeviceCapabilities(Capabilities caps, String deviceId) 
    throws AdminException {
    
        Long idCaps = businessDelegate.setDeviceCapabilities(caps, deviceId);
        caps.setId(idCaps);

        Log.info(Bundle.getMessage(Bundle.CAPABILITIES_MESSAGE_INSERT_OK));
    }
    
    /**
     * Get the device capabilities for the specified device identifier.
     *
     * @param deviceId the device's identifier
     *
     * @throws AdminException
     *
     * @return capabilities for specified device's identifier
     */
    public Capabilities getDeviceCapabilities(String deviceId) 
    throws AdminException {
    
        return businessDelegate.getDeviceCapabilities(deviceId);        
    }
    
    /**
     * Method for search devices. also display the results.
     * @param clause Clause the clause for search in the devices
     * @throws AdminException
     */
    public void searchDevices(Clause clause) throws AdminException {
        int maxNumOfResults = AdminConfig.getAdminConfig().getMaxResults();

        int numOfDevices = businessDelegate.countDevices(clause);

        String message = null;
        switch (numOfDevices) {
            case 0:
                message = Bundle.getMessage(Bundle.DEVICE_MESSAGE_DEVICE_NOT_FOUND);
                break;
            case 1:
                message = Bundle.getMessage(Bundle.DEVICE_MESSAGE_DEVICE_FOUND);
                break;
            default:
                message = MessageFormat.format(Bundle.getMessage(Bundle.
                    DEVICE_MESSAGE_DEVICES_FOUND),
                    new String[] {String.valueOf(numOfDevices)});
        }

        Log.info(message);

        if (numOfDevices > maxNumOfResults) {
            Object[] arguments = {new Integer(numOfDevices)};

            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(Bundle.getMessage(Bundle.MESSAGE_MAX_NUMBER_OF_RESULTS),
                                     arguments));
            DialogDisplayer.getDefault().notify(desc);

            return;
        }

        // search devices with clause
        Sync4jDevice[] devices = businessDelegate.getDevices(clause);

        // load values
        SearchDevicePanel searchDevicePanel = (SearchDevicePanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.SEARCH_DEVICE_PANEL_NAME));

        if (searchDevicePanel != null) {
            searchDevicePanel.loadDevices(devices);
        }

    }

    /**
     * Update the specified device.
     * @param device Sync4jDevice the device to be updated.
     * @throws AdminException
     * @return boolean
     */
    public boolean updateDevice(Sync4jDevice device) throws AdminException {
        boolean deviceUpdated = false;

        boolean deviceOk = checkDeviceValue(device);

        if (!deviceOk) {
            return deviceUpdated;
        }

        // update values on server
        Log.debug("Device to update: " + device);

        businessDelegate.setDevice(
            device);

        Log.info(Bundle.getMessage(Bundle.DEVICE_MESSAGE_SAVE_OK));
        deviceUpdated = true;

        return deviceUpdated;

    }

    /**
     * Display a notification message to ask user if he wants to delete value of
     * the selected device.
     */
    private boolean showConfirmDelete() {
        boolean ret = false;
        // prepare notification message
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(
            Bundle.getMessage(Bundle.DEVICE_QUESTION_DELETE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_DELETE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        // ok , so update values
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {
            ret = true;
        }
        // cancel, so restore old values
        if (nd.getValue() == NotifyDescriptor.CANCEL_OPTION) {
            ret = false;
        }
        // close , so restore old values
        if (nd.getValue() == NotifyDescriptor.CLOSED_OPTION) {
            ret = false;
        }
        return ret;
    }

    /**
     * Create and return the clause for the device search
     * @param id String the value for the create clause
     * @return Clause
     */
    private Clause getClauseForDeviceSearchById(String id) {
        Clause clause = null;

        String[] param = {PARAM_ID};
        String[] value = {id};
        String[] operator = {WhereClause.OPT_EQ};

        clause = ClauseTools.createClause(param, operator, value);

        return clause;
    }

    /**
     * checks the device values, and notify any wrong parameter in the device.
     * @param device Sync4jDevice the specified device
     * @return boolean
     */
    private boolean checkDeviceValue(Sync4jDevice device) {

        String id = device.getDeviceId();
        String type = device.getType();
        String description = device.getDescription();

        // controls about edited fields
        String check = "";
        if (id.equalsIgnoreCase("") || id.equalsIgnoreCase(null)) {
            check = check + Bundle.getMessage(Bundle.DEVICE_PANEL_ID) + ", ";
        }

        if (!check.equalsIgnoreCase("")) {
            // create error message and display it
            check = Bundle.getMessage(Bundle.INPUT_DATA_ERROR_MESSAGE) + " " +
                    check.substring(0, check.length() - 2) + ".";
            NotifyDescriptor desc = new NotifyDescriptor.Message(check);
            DialogDisplayer.getDefault().notify(desc);

            return false;
        }

        return true;

    }

}
