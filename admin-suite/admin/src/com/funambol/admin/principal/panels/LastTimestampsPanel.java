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
package com.funambol.admin.principal.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.MessageFormat;

import javax.print.attribute.DateTimeSyntax;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.sql.Timestamp;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;

import com.funambol.framework.core.AlertCode;
import com.funambol.framework.core.StatusCode;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.LastTimestamp;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Create a panel to view information about synchronization
 *
 * @version $Id: LastTimestampsPanel.java,v 1.9 2007-11-28 10:28:17 nichele Exp $
 **/
public class LastTimestampsPanel
extends JPanel 
implements ActionListener, ListSelectionListener {

    // ------------------------------------------------------------ Private data
    /** the principal wich the last timestamps serch is based on  */
    private Sync4jPrincipal principal = null;

    /** 
     * Getter for property principal.
     *
     * @return Value of property principal.
     */
    public Sync4jPrincipal getPrincipal() {
        return principal;
    }

    /** 
     * Setter for property principal.
     *
     * @param principal New value of property principal.     
     */
    public void setPrincipal(Sync4jPrincipal principal) {
        this.principal = principal;

        principalId.setText(String.valueOf(principal.getId()));
        userName.setText(principal.getUsername());
        deviceId.setText(principal.getDeviceId());
    }

    /** label for the panel's name  */
    private JLabel title = null;

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1 = null;

    /** label for user */
    private JLabel jLUser = null;

    /** label for device */
    private JLabel jLDevice = null;

    /** label for principal */
    private JLabel jLPrincipal = null;

    /** principals id value */
    private JLabel principalId = null;

    /** user name value */
    private JLabel userName = null;

    /** device id value */
    private JLabel deviceId = null;

    /** Split Main Panel .. */
    private JSplitPane splitMainPanel = null;

    /** Split South Panel .. */
    private JSplitPane splitSouthPanel = null;

    /** Top Panel .. */
    private JPanel northPanel = null;

    /** Bottom Panel .. */
    private JPanel southPanel = null;

    /** Table Panel .. */
    private JPanel tablePanel = null;

    /** Buttons Panel .. */
    private JPanel buttonsPanel = null;

    /** model for result of search */
    private MyTableModel model = null;

    /** table for principal data */
    private JTable table = null;

    /**  button to remove principals last synchronizations timestamp  */
    private JButton jBReset = null;
    
    /** button to refresh the list of timestamps information */
    private JButton jBRefresh = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Generate the panel
     */
    public LastTimestampsPanel() {

        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }
    // --------------------------------------------------------- Private methods

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        this.setLayout(new BorderLayout());
        this.setName(Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_NAME));

        JScrollPane formPanel = new JScrollPane(this);
        formPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.setBorder(BorderFactory.createEmptyBorder());

        initNorthPanel();
        initSouthPanel();

        splitMainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, northPanel, southPanel);
        splitMainPanel.setDividerSize(0);
        splitMainPanel.setDividerLocation(140);
        splitMainPanel.setBorder(BorderFactory.createEmptyBorder());

        this.add(splitMainPanel, BorderLayout.CENTER);
    }

    /**
     * Create the top panel
     */
    private void initNorthPanel() {

        northPanel = new JPanel();
        northPanel.setLayout(null);

        titledBorder1 = new TitledBorder("");

        title = new JLabel();
        title.setText(Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_NAME));
        title.setBounds(new Rectangle(14, 5, 316, 28));
        title.setAlignmentX(SwingConstants.CENTER);
        title.setBorder(titledBorder1);

        // set properties of label and textfield referred to principalID including position
        jLPrincipal = new JLabel();
        jLPrincipal.setBounds(new Rectangle(13, 53, 76, 20));
        jLPrincipal.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_PRINCIPALID) + " :");
        principalId = new JLabel();
        principalId.setBounds(new Rectangle(91, 53, 200, 20));

        // set properties of label and textfield referred to username including position
        jLUser = new JLabel();
        jLUser.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_USERNAME) + " :");
        jLUser.setBounds(new Rectangle(13, 78, 76, 20));
        userName = new JLabel();
        userName.setBounds(new Rectangle(91, 78, 200, 20));

        // set properties of label and textfield referred to deviceid including position
        jLDevice = new JLabel();
        jLDevice.setBounds(new Rectangle(13, 103, 76, 20));
        jLDevice.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_DEVICEID) + " :");
        deviceId = new JLabel();
        deviceId.setBounds(new Rectangle(91, 103, 200, 20));

        title.setFont(GuiFactory.titlePanelFont);
        jLUser.setFont(GuiFactory.defaultFont);
        jLDevice.setFont(GuiFactory.defaultFont);
        jLPrincipal.setFont(GuiFactory.defaultFont);
        userName.setFont(GuiFactory.defaultFont);
        deviceId.setFont(GuiFactory.defaultFont);
        principalId.setFont(GuiFactory.defaultFont);

        northPanel.add(title, null);
        northPanel.add(jLPrincipal, null);
        northPanel.add(principalId, null);
        northPanel.add(jLUser, null);
        northPanel.add(userName, null);
        northPanel.add(jLDevice, null);
        northPanel.add(deviceId, null);

    }

    /**
     * Create the bottom panel
     */
    private void initSouthPanel() {

        southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.setBorder(BorderFactory.createEtchedBorder());

        initTablePanel();
        initButtonsPanel();

        southPanel.add(tablePanel, BorderLayout.CENTER);
        southPanel.add(buttonsPanel, BorderLayout.EAST);

    }

    /**
     * Create the table panel
     */
    private void initTablePanel() {

        tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        tablePanel.setPreferredSize(new Dimension(650, 200));

        model = new MyTableModel();
        table = new JTable(model);

        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(159);
        table.getColumnModel().getColumn(2).setPreferredWidth(35);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(800, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);

        tablePanel.add(scrollpane, BorderLayout.CENTER);
    }

    /**
     * Create the buttons panel
     */
    private void initButtonsPanel() {

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);
        buttonsPanel.setPreferredSize(new Dimension(150, 200));

        jBRefresh = new JButton();
        jBRefresh.setBounds(new Rectangle(35, 50, 79, 27));
        jBRefresh.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_REFRESH));
        jBRefresh.setActionCommand(Constants.ACTION_COMMAND_REFRESH_TIMESTAMP);
        jBRefresh.setEnabled(true);

        jBReset = new JButton();
        jBReset.setBounds(new Rectangle(35, 100, 79, 27));
        jBReset.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_RESET));
        jBReset.setActionCommand(Constants.ACTION_COMMAND_RESET_TIMESTAMP);

        buttonsPanel.add(jBRefresh, null);
        buttonsPanel.add(jBReset  , null);
        
        jBRefresh.addActionListener(this);
        jBReset.addActionListener(this);
        
    }

    /**
     * Enable or disable the button
     * @param enabled true to enable the button, otherwise false
     */
    public void enableButtons(boolean enabled) {
        jBReset.setEnabled  (enabled);        
    }

    /**
     * Loads LastTimestamps in the table
     * @param lastTimestamps LastTimestamp[]
     */
    public void loadLastTimestamps(LastTimestamp[] lastTimestamps) {

        model.loadLastTimestamps(lastTimestamps);

        if (lastTimestamps != null && lastTimestamps.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);
            enableButtons(true);
        }
        else {
            enableButtons(false);
        }

        table.updateUI();
    }

    /**
     *
     * @return selected sync source
     */
    public LastTimestamp getSelectedTimestamp() {

        int row = table.getSelectedRow();
        LastTimestamp timestamp = model.lastTimestamps[row];

        return timestamp;
    }

    /**
     * Delete a row from the table
     * @param row int
     */
    public void deleteRowInTable(int row) {
        model.deleteRow(row);

    }

    /**
     * Returns the number of selected rows.
     * @return the number of selected rows, 0 if no rows are selected
     */
    public int getNumRowsSelected() {
        return table.getSelectedRowCount();
    }

    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed. Cases are:<br>
     * 1- delete : delete principal with the selected values <br>
     * 2- search : search principal with form parameters and
     *  display results in user result table.<br>
     * 3- insert : insert principal.<br>
     * @param event ActionEvent
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Log.debug("Process " + command + " command");

        String description = null;
        try {
            if (command.equals(Constants.ACTION_COMMAND_RESET_TIMESTAMP)) {
                description = Bundle.getMessage(Bundle.DELETING);
                if (table.getSelectedRow() == -1) {
                    NotifyDescriptor desc = new NotifyDescriptor.Message(
                        Bundle.getMessage(Bundle.NO_DATA_SELECTED));
                    DialogDisplayer.getDefault().notify(desc);
                } else {
                    LastTimestamp timestamp = getSelectedTimestamp();

                    boolean timestampDeleted = false;

                    timestampDeleted = ExplorerUtil.getSyncAdminController()
                                                   .getPrincipalsController()
                                                   .deleteLastTimestamp(timestamp);
                    if (timestampDeleted) {
                        int row = table.getSelectedRow();
                        deleteRowInTable(row);

                        int count = table.getRowCount();
                        if(count > 0) {
                            table.setRowSelectionInterval(0, 0);
                            enableButtons(true);
                        } else {
                            enableButtons(false);
                        }
                    }
                }
            } else if (command.equals(Constants.ACTION_COMMAND_REFRESH_TIMESTAMP)) {
                ExplorerUtil.getSyncAdminController()
                            .getPrincipalsController()
                            .startLastTimestampsProcess(getPrincipal());
            }

        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_LAST_TIMESTAMP),
                    new String[] { description }
                )
            );
        }
    }

    /**
     * Enables delete button if values is selected
     * @param event ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent event) {
        int numTimestampsSelected = getNumRowsSelected();

        if (numTimestampsSelected > 0) {
            jBReset.setEnabled(true);
        } else {
            jBReset.setEnabled(false);
        }
    }

    //-----------------------------------------------
    // Overrides javax.swing.JComponent method
    //-----------------------------------------------
    /**
     * Set size of the panel
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }

    /**
     * <p>Create the model of the table.
     * The fields associated to a princiapl (princiaplID, user, deviceID) are
     * managed in the model</p>
     */
    public class MyTableModel extends AbstractTableModel {

        // -------------------------------------------------------- Private data
        /** princiapls to display in table */
        private LastTimestamp[] lastTimestamps = null;

        /** properties of the table, contains the column names */
        private String[] columnNames = new String[] {
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_DATABASE),
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_SYNC_TYPE) ,
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_STATUS)    ,
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_TAG_CLIENT),
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_TAG_SERVER),
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_START),
                                       Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_END)};

        /** container of datas in the table  */
        private Object[][] rowData = null;

        // -------------------------------------------------------- Constructors
        /**
         * void constructor
         */
        public MyTableModel() {
        }

        // ------------------------------------------------------ Public methods

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
         * Returns number of columns
         * @return columns number
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Delete a row from the table.
         * @param row target to delete
         */
        public void deleteRow(int row) {
            int col = getColumnCount();
            int rows = getRowCount() - 1;
            boolean deleted = false;
            Object[][] oldRowData = rowData;
            rowData = new Object[rows][columnNames.length];

            LastTimestamp[] oldLastTimestampArray = lastTimestamps;
            lastTimestamps = new LastTimestamp[rows];

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
                    lastTimestamps[i] = oldLastTimestampArray[i];
                } else {
                    lastTimestamps[i] = oldLastTimestampArray[i + 1];
                }

            }
            model.fireTableDataChanged();

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
            return rowData[0][column].getClass();
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Set if cell in the table is editable.
         * First column is not editable, other are editable
         * @param row selected row
         * @param col selected column
         * @return true if cell is editable
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * Set value of the principal in the table and save a copy of the old values if edited
         * @param value princiapl value to insert in the table
         * @param row selected row
         * @param col selected column
         */
        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value;
            LastTimestamp lastTimestamp = lastTimestamps[row];
            if (col == 0) {
                lastTimestamp.database = (String)value;
            } else if (col == 1) {
                lastTimestamp.syncType = ((Integer)value).intValue();
            } else if (col == 2) {
                lastTimestamp.status = ((Integer)value).intValue();
            } else if (col == 3) {
                lastTimestamp.tagClient = (String)value;
            } else if (col == 4) {
                lastTimestamp.tagServer = (String)value;
            } else if (col == 5) {
                lastTimestamp.start =  Timestamp.valueOf((String)value).getTime();
            } else if (col == 6) {
                lastTimestamp.end = Timestamp.valueOf((String)value).getTime();
            }
        }

        /**
         * Load values of recived principals into table
         * @param devices
         */
        public void loadLastTimestamps(LastTimestamp[] lastTimestamps) {

            int size = lastTimestamps.length;
            this.lastTimestamps = lastTimestamps;
            rowData = new Object[size][columnNames.length];

            Timestamp time = null;
            for (int i = 0; i < size; i++) {
                rowData[i][0] = lastTimestamps[i].database;

                Integer syncType = new Integer(lastTimestamps[i].syncType);
                rowData[i][1] = AlertCode.getAlertDescription(syncType.intValue());

                if (lastTimestamps[i].status == 0) {
                    rowData[i][2] = new String();
                } else {
                    rowData[i][2] = String.valueOf(lastTimestamps[i].status);
                }

                rowData[i][3] = lastTimestamps[i].tagClient;

                rowData[i][4] = lastTimestamps[i].tagServer;

                time = new Timestamp(lastTimestamps[i].start);
                rowData[i][5] = time.toString();

                time = new Timestamp(lastTimestamps[i].end);
                rowData[i][6] = time.toString();
            }

        }

  }

}

