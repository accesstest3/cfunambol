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
package com.funambol.admin.device.panels;

import java.text.MessageFormat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.Sync4jDevice;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Create the panel used for display results of searching device.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a device (deviceID, Type, Description) are display
 * in the table through the model.
 *
 * @version $Id: SearchDevicePanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 *
 **/
public class SearchDevicePanel 
extends JPanel 
implements ActionListener, ListSelectionListener {

    // ------------------------------------------------------------ Private data

    /** global split panel */
    private JSplitPane splitPanel = null;

    /** panel for form */
    private FormSearchDevicePanel formSearchDevicePanel = null;

    /** panel for results of search */
    private JPanel resultSearchDevicePanel = null;

    /** panel for table */
    private ResultSearchDevicePanel tableResultSearchDevicePanel = null;

    /** panel for button cancel */
    private ButtonResultSearchDevicePanel buttonResultSearchDevicePanel = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create panel that permitt to manage device.<br>
     * The panel is composed of:<br>
     * a form to search device,<br>
     * a table for the result of the search,<br>
     * a cancel button to delete a device.
     */
    public SearchDevicePanel() {
        // set global parameters and properties of the panel
        this.setLayout(new BorderLayout());
        this.setName(Bundle.getMessage(Bundle.SEARCH_DEVICE_PANEL_NAME));

        // create the panels
        formSearchDevicePanel = new FormSearchDevicePanel();

        tableResultSearchDevicePanel = new ResultSearchDevicePanel();
        tableResultSearchDevicePanel.addListSelectionListenerToTable(this);

        buttonResultSearchDevicePanel = new ButtonResultSearchDevicePanel();

        formSearchDevicePanel.addFormActionListener(this);
        buttonResultSearchDevicePanel.addButtonActionListener(this);

        // create a panel for search result table and right button
        resultSearchDevicePanel = new JPanel();
        resultSearchDevicePanel.setLayout(new BorderLayout());
        resultSearchDevicePanel.add(tableResultSearchDevicePanel, BorderLayout.CENTER);
        resultSearchDevicePanel.add(buttonResultSearchDevicePanel, BorderLayout.EAST);

        // create form panel with scrollbar
        JScrollPane formPanel = new JScrollPane(formSearchDevicePanel);
        formPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.setBorder(BorderFactory.createEmptyBorder());

        // create main panel with scrollbar
        JScrollPane resultPanel = new JScrollPane(resultSearchDevicePanel);
        resultPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultPanel.setBorder(BorderFactory.createEtchedBorder());

        // create final panel with form at the top and table+button at the center
        splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, formPanel, resultPanel);
        splitPanel.setDividerSize(0);
        splitPanel.setDividerLocation(210);
        splitPanel.setBorder(BorderFactory.createEmptyBorder());

        this.add(splitPanel, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the modified device
     * @return modified device
     */
    public Sync4jDevice getDeviceModify() {
        return tableResultSearchDevicePanel.getDeviceModify();
    }

    /**
     * Stops cell editing
     */
    public void stopEditing() {
        // check if user is editing cell in table
        if (tableResultSearchDevicePanel.getTable().isEditing()) {
            // set table as changed
            tableResultSearchDevicePanel.setChanged();
            // stop cell editing
            tableResultSearchDevicePanel.getTable().getCellEditor().stopCellEditing();
        }
    }

    /**
     * Returns true if there is a modified device
     * @return boolean
     */
    public boolean hasDeviceChanged() {
        return tableResultSearchDevicePanel.isChanged();
    }

    /**
     * Set no change in table
     */
    public void setNotChanged() {
        tableResultSearchDevicePanel.setNotChanged();
    }

    /**
     * Returns the device to delete
     * @return device to delete
     */
    public Sync4jDevice getDeviceToDelete() {
        return tableResultSearchDevicePanel.getDeviceToDelete();
    }

    /**
     * Deletes selected Row
     */
    public void confirmDeviceDeleted() {
        tableResultSearchDevicePanel.deleteSelectedRow();
    }

    /**
     * Returns clause created by input values in the search form
     * @return Clause
     */
    public Clause getSearchClause() {
        return formSearchDevicePanel.getClause();
    }

    /**
     * Load devices in the result table
     * @param devices Sync4jDevice[]
     */
    public void loadDevices(Sync4jDevice[] devices) {
        tableResultSearchDevicePanel.loadDevices(devices);
        if (devices != null && devices.length > 0) {
            buttonResultSearchDevicePanel.enableButtons(true);
        } else {
            buttonResultSearchDevicePanel.enableButtons(false);
        }
    }

    /**
     * Add the listener recived as parameter to the panel
     * @param listener ActionListener
     */
    public void addActionListener(ActionListener listener) {
        // add this panel as listener of other's panel button
        formSearchDevicePanel.addFormActionListener(listener);
        buttonResultSearchDevicePanel.addButtonActionListener(listener);
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Enable buttons if value is changed
     * @param event ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent event) {
        int numRowsSelected = tableResultSearchDevicePanel.getNumRowsSelected();
        if (numRowsSelected > 0) {
            buttonResultSearchDevicePanel.enableButtons(true);
        } else {
            buttonResultSearchDevicePanel.enableButtons(false);
        }
    }

    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed. Cases are:<br>
     * 1- modify : change device values with the edited one <br>
     * 2- cancel : delete selected row from the table and associated device from server<br>
     * 3- search : search device with form parameters and display results in table.<br>
     * @param event
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Log.debug("Process " + command + " command");

        String description = null;
        try {
            // case : modify
            if (command.equals(Constants.ACTION_COMMAND_SAVE_DEVICE)) {
                description = Bundle.getMessage(Bundle.UPDATING);

                // check if user is editing cell in table
                if (tableResultSearchDevicePanel.getTable().isEditing()) {
                    // set table as changed
                    tableResultSearchDevicePanel.setChanged();
                    // stop cell editing
                    tableResultSearchDevicePanel.getTable().getCellEditor().stopCellEditing();
                }
                if (tableResultSearchDevicePanel.isChanged()) {

                    boolean deviceUpdated = false;

                    deviceUpdated = ExplorerUtil.getSyncAdminController()
                                                .getDevicesController()
                                                .updateDevice(tableResultSearchDevicePanel.getDeviceModify());

                    if (deviceUpdated) {
                        // updated values on server, so set table to not changed
                        tableResultSearchDevicePanel.setNotChanged();
                    }

                } else {
                    // no value changed, so do nothing
                }
            } else if (command.equals(Constants.ACTION_COMMAND_DELETE_DEVICE)) {
                description = Bundle.getMessage(Bundle.DELETING);

                // check if a row is selected
                if (tableResultSearchDevicePanel.getTable().getSelectedRow() == -1) {
                    // display alert because no row selectd
                    NotifyDescriptor desc = new NotifyDescriptor.Message(
                        Bundle.getMessage(Bundle.NO_DATA_SELECTED));
                    DialogDisplayer.getDefault().notify(desc);
                } else {

                    Sync4jDevice deviceToDelete = tableResultSearchDevicePanel.getDeviceToDelete();
                    boolean deviceDeleted = false;

                    deviceDeleted = ExplorerUtil.getSyncAdminController()
                                                .getDevicesController()
                                                .deleteDevice(deviceToDelete);

                    if (deviceDeleted) {
                        // delete row from table
                        tableResultSearchDevicePanel.deleteSelectedRow();
                    }
                }
            } else if (command.equals(Constants.ACTION_COMMAND_EDIT_DEVICE)) {
                description = Bundle.getMessage(Bundle.EDITING);

                Sync4jDevice deviceToEdit = tableResultSearchDevicePanel.getDeviceSelected();
                ExplorerUtil.getSyncAdminController().getDevicesController()
                    .startEditDeviceProcess(deviceToEdit);
            } else if (command.equals(Constants.ACTION_COMMAND_SEARCH_DEVICE)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                // create clause for the search
                Clause clause = formSearchDevicePanel.getClause();

                ExplorerUtil.getSyncAdminController().getDevicesController().searchDevices(clause);

            } else if (command.equals(Constants.ACTION_COMMAND_NEW_DEVICE)) {
                ExplorerUtil.getSyncAdminController().getDevicesController().startNewDeviceProcess();
            } else if (command.equals(Constants.ACTION_COMMAND_DEVICE_CAPS)) {
                description = Bundle.getMessage(Bundle.SEARCHING);
                
                ExplorerUtil.getSyncAdminController()
                            .getDevicesController()
                            .startDevInfProcess(tableResultSearchDevicePanel.getDeviceSelected());                
                
            }
        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_DEVICE),
                    new String[] { description }
                ),
                e
            );
        }
    }

}
