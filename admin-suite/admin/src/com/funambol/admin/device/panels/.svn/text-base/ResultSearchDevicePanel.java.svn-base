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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;

import com.funambol.framework.server.Sync4jDevice;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.ObjectsSortTools;

/**
 * Create the panel used for display results of searching device.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a device (deviceID, Type, Description) are display
 * in the table through the model.
 *
 * @version $Id: ResultSearchDevicePanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 **/
public class ResultSearchDevicePanel extends JPanel {

    // ------------------------------------------------------------ Private data

    /** model for result of search */
    private DevicesTableModel model = null;

    /** table for device data */
    private JTable table = null;

    /** parameter used if data in table change */
    private boolean changed = false;

    /** copy of device for restore old data discarding new edited values */
    private Sync4jDevice deviceBackup = new Sync4jDevice();

    /** device with modified parameter when editing */
    private Sync4jDevice deviceModify = null;

    /** int used to remember row edited */
    private int oldRow = 0;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for search device result.
     */
    public ResultSearchDevicePanel() {
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Pass devices recived to model and update table to refresh values
     * @param devices devices array to load in the table
     */
    public void loadDevices(Sync4jDevice[] devices) {
        ObjectsSortTools.sort("deviceId", devices);
        model.loadDevices(devices);
        if (devices != null && devices.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);
        }
        table.updateUI();
    }

    /**
     * Returns the selected device.
     * @return Sync4jDevice the device selected
     */
    public Sync4jDevice getDeviceSelected() {
        int row = table.getSelectedRow();
        Sync4jDevice deviceSelected = model.devices[row];
        return deviceSelected;
    }

    /**
     * Returns device value to delete from server
     * @return device value to delete from server
     */
    public Sync4jDevice getDeviceToDelete() {
        int row = table.getSelectedRow();
        Sync4jDevice deviceToDelete = model.devices[row];
        return deviceToDelete;
    }

    /**
     * Display a notification message because values in the table are changed
     * and the row is changed, so ask to user if he wants to update value or
     * restore the old ones. It also perform the action on the server
     */
    public void showNotification() {
        // prepare notification message
        Confirmation nd = new Confirmation(
            Bundle.getMessage(Bundle.DEVICE_QUESTION_UPDATE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);


        // ok , so update values
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {

            boolean deviceUpdated = false;

            try {
                deviceUpdated = ExplorerUtil.getSyncAdminController().getDevicesController().
                                updateDevice(deviceModify);
            } catch (AdminException e) {
                Log.error(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.ERROR_HANDLING_DEVICE),
                        new String[] {Bundle.getMessage(Bundle.UPDATING)}
                    ),
                    e
                );
            }

            if (deviceUpdated) {
                changed = false;
            } else {
                // update failed
                // reselect previous row, but before set changed = false for to disable
                // the action of the listener on the table

                changed = false;
                table.getSelectionModel().setSelectionInterval(oldRow, oldRow);

                // re-set changed = true for to enable the action of the listener on the table
                changed = true;
            }

        }
        // cancel, so restore old values
        if (nd.getValue() == NotifyDescriptor.CANCEL_OPTION) {
            model.setDevice(deviceBackup, oldRow);
            changed = false;
        }
        // close , so restore old values
        if (nd.getValue() == NotifyDescriptor.CLOSED_OPTION) {
            model.setDevice(deviceBackup, oldRow);
            changed = false;
        }
    }

    /**
     * Set private parameter <code>changed</code> to not changed
     */
    public void setNotChanged() {
        this.changed = false;
    }

    /**
     * Set private parameter <code>changed</code> to changed
     */
    public void setChanged() {
        this.changed = true;
    }

    /**
     * Checks values in the cells
     * @return true if values in the cells of the table are changed
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * Returns device modifications
     * @return device with new edited values
     */
    public Sync4jDevice getDeviceModify() {
        return this.deviceModify;
    }

    /**
     * Returns table
     * @return table contained in the panel
     */
    public JTable getTable() {
        return this.table;
    }

    /**
     * Delete selected row from table
     */
    public void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row != -1) {
            model.deleteRow(row);
        }
    }

    /**
     * Returns the number of selected rows.
     * @return the number of selected rows, 0 if no rows are selected
     */
    public int getNumRowsSelected() {
        return table.getSelectedRowCount();
    }

    /**
     * Add <code>ListSelecctionListener</code> to the table's selection model
     * @param listener listener to add
     */
    public void addListSelectionListenerToTable(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 250);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {
        // create objects to display
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());

        model = new DevicesTableModel();
        table = new JTable(model);
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(800, 300));

        // add listener to get changes on table values
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }

                if (event.getSource() == table.getSelectionModel() &&
                    table.getRowSelectionAllowed()) {
                    if (changed) {
                        showNotification();
                    }
                }
            }
        });

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        ExplorerUtil.getSyncAdminController().getDevicesController().
                            startEditDeviceProcess(
                                getDeviceSelected());
                    } catch (AdminException e) {
                        String description = Bundle.getMessage(Bundle.EDITING);
                        Log.error(
                            MessageFormat.format(
                                Bundle.getMessage(Bundle.ERROR_HANDLING_DEVICE),
                                new String[] {description}
                            ),
                            e
                        );
                    }
                }
            }
        });
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);

        this.add(scrollpane, BorderLayout.CENTER);
    }

    /**
     * <p>Create the model of the table.
     * The fields associated to a device (deviceID, Type, Description) are
     * managed in the model</p>
     */
    public class DevicesTableModel extends AbstractTableModel {

        // -------------------------------------------------------- Private data
        /** devices to display in table */
        private Sync4jDevice[] devices = null;

        /** properties of the table, contains the column names */
        private String[] columnNames = new String[] {
                                       Bundle.getMessage(Bundle.DEVICE_PANEL_ID),
                                       Bundle.getMessage(Bundle.DEVICE_PANEL_TYPE),
                                       Bundle.getMessage(Bundle.DEVICE_PANEL_ADDRESS),
                                       Bundle.getMessage(Bundle.DEVICE_PANEL_DESCRIPTION)};

        /** container of datas in the table  */
        private Object[][] rowData = null;

        // -------------------------------------------------------- Constructors

        /**
         * void constructor
         */
        public DevicesTableModel() {
        }

        // ------------------------------------------------------ Public Methods

        /**
         * Returns rows number
         * @return rows number
         */
        public int getRowCount() {
            if (rowData == null) {
                return 0;
            } else {
                return rowData.length;
            }
        }

        /**
         * Returns columns number
         * @return columns number
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Returns value in the table
         * @param row origin row
         * @param column origin column
         * @return value in the table
         */
        public Object getValueAt(int row, int column) {
            return rowData[row][column];
        }

        /**
         * Delete a row from the table.
         * @param row target to delete
         */
        public void deleteRow(int row) {
            Log.debug("ResultSearchDevicePanel : deleteRow(): " + row);
            int col = getColumnCount();
            int rows = getRowCount() - 1;
            boolean deleted = false;
            Object[][] oldRowData = rowData;
            rowData = new Object[rows][columnNames.length];

            Sync4jDevice[] oldDeviceArray = devices;
            devices = new Sync4jDevice[rows];

            for (int i = 0; i < rows; i++) {
                if (i == row) {
                    deleted = true;
                }
                for (int y = 0; y < col; y++) {
                    if (deleted) {
                        rowData[i][y] = oldRowData[i + 1][y];
                        model.fireTableCellUpdated(i, y);
                    } else {
                        rowData[i][y] = oldRowData[i][y];
                        model.fireTableCellUpdated(i, y);
                    }
                }

                if (!deleted) {
                    devices[i] = oldDeviceArray[i];
                } else {
                    devices[i] = oldDeviceArray[i + 1];
                }
            }
            model.fireTableDataChanged();
        }

        /**
         * Load values of recived devices into table
         * @param devices
         */
        public void loadDevices(Sync4jDevice[] devices) {
            int size = devices.length;
            this.devices = devices;
            rowData = new Object[size][columnNames.length];
            for (int i = 0; i < size; i++) {
                rowData[i][0] = (devices[i].getDeviceId());
                rowData[i][1] = (devices[i].getType());
                rowData[i][2] = ((devices[i].getAddress()));
                rowData[i][3] = (devices[i].getDescription());
            }
        }

        /**
         * update values in the table and in private data <code>devices</code>
         * @param device
         * @param row
         */
        public void setDevice(Sync4jDevice device, int row) {
            // change global Sync4jDevice
            devices[row].setDeviceId(device.getDeviceId());
            devices[row].setType(device.getType());
            devices[row].setAddress(device.getAddress());
            devices[row].setDescription(device.getDescription());

            model.setValueAt(device.getDeviceId(), row, 0);
            model.fireTableCellUpdated(row, 0);
            model.setValueAt(device.getType(), row, 1);
            model.fireTableCellUpdated(row, 1);
            model.setValueAt(device.getDescription(), row, 2);
            model.fireTableCellUpdated(row, 2);
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Returns the name of the column
         * @param column origin column
         * @return name of the column
         */
        public String getColumnName(int column) {
            return columnNames[column];
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Returns classname of the value in the table
         * @param column
         * @return Classname of the value in the table
         */
        public Class getColumnClass(int column) {
            return String.class;
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Sets cells in the table not editable.
         * @param row selected row
         * @param col selected column
         * @return
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Set value of the device in the table and save a copy of the old values if edited
         * @param value device value to insert in the table
         * @param row selected row
         * @param col selected column
         */
        public void setValueAt(Object value, int row, int col) {
            // check if values are changed in table to correct double click
            if ( ( (String)rowData[row][col]).equals( (String)value) && !changed) {
                Log.debug("Value not changed");
                changed = false;
            } else {
                if (!changed) {
                    //save old param into deviceBackup
                    deviceBackup.setDeviceId(devices[row].getDeviceId());
                    deviceBackup.setType(devices[row].getType());
                    deviceBackup.setAddress(devices[row].getAddress());
                    deviceBackup.setDescription(devices[row].getDescription());
                    oldRow = row;
                }

                rowData[row][col] = value;
                Sync4jDevice device = devices[row];
                if (col == 0) {
                    device.setDeviceId( (String)value);
                }
                if (col == 1) {
                    device.setType( (String)value);
                }
                if (col == 2) {
                    device.setAddress( (String)value);
                }
                if (col == 3) {
                    device.setDescription( (String)value);
                }
                deviceModify = device;
                changed = true;
            }
        }

    }

}
