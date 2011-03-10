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

package com.funambol.admin.principal.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import java.text.MessageFormat;

import com.funambol.framework.security.Sync4jPrincipal;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.ObjectsSortTools;

/**
 * Create the panel used for display results of searching principal.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a device (princiaplID, user, deviceID) are display
 * in the table through the model.
 *
 * @version $Id: ResultSearchPrincipalPanel.java,v 1.10 2007-11-28 10:28:17 nichele Exp $
 **/
public class ResultSearchPrincipalPanel extends JPanel {

    // ------------------------------------------------------------ Private data
    /** model for result of search */
    private MyTableModel model = null;

    /** table for principal data */
    private JTable table = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Generate the panel
     */
    public ResultSearchPrincipalPanel() {
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    // --------------------------------------------------------- Public methods

    /**
     * Loads Principal in the table
     * @param principals Sync4jPrincipal[]
     */
    public void loadPrincipals(Sync4jPrincipal[] principals) {

        ObjectsSortTools.sort("id", ObjectsSortTools.TYPE_INTEGER, principals);

        model.loadPrincipals(principals);

        if (principals != null && principals.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);
        }

        table.updateUI();
    }

    /**
     *
     * @return principal to delete
     */
    public Sync4jPrincipal getPrincipalToDelete() {

        int row = table.getSelectedRow();
        int rows = table.getSelectedRowCount();
        Sync4jPrincipal principalToDelete = model.principals[row];

        return principalToDelete;
    }

    /**
     *
     * @return selected principal
     */
    public Sync4jPrincipal getSelectedPrincipal() {

        int row = table.getSelectedRow();
        int rows = table.getSelectedRowCount();
        Sync4jPrincipal principal = model.principals[row];

        return principal;
    }

    /**
     * Returns table
     * @return table
     */
    public JTable getTable() {
        return this.table;
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

    /**
     * Add <code>ListSelecctionListener</code> to the table's selection model
     * @param listener listener to add
     */
    public void addListSelectionListenerToTable(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
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

    // --------------------------------------------------------- Private methods

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        // create objects to display
        this.setLayout(new BorderLayout());
        model = new MyTableModel();
        table = new JTable(model);
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(800, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        ExplorerUtil.getSyncAdminController()
                                    .getPrincipalsController()
                                    .startLastTimestampsProcess(getSelectedPrincipal());
                        
                    } catch (AdminException e) {
                        String description = Bundle.getMessage(Bundle.EDITING);
                        Log.error(
                            MessageFormat.format(
                                Bundle.getMessage(Bundle.ERROR_HANDLING_PRINCIPAL),
                                new String[] {description}
                            ),
                            e
                        );
                    }
                }
            }
        });
        
        this.add(scrollpane, BorderLayout.CENTER);
    }

    /**
     * <p>Create the model of the table.
     * The fields associated to a princiapl (princiaplID, user, deviceID) are
     * managed in the model</p>
     */
    public class MyTableModel extends AbstractTableModel {

        // -------------------------------------------------------- Private data
        /** princiapls to display in table */
        private Sync4jPrincipal[] principals = null;

        /** properties of the table, contains the column names */
        private String[] columnNames = new String[] {
                                       Bundle.getMessage(Bundle.PRINCIPAL_PANEL_PRINCIPALID),
                                       Bundle.getMessage(Bundle.PRINCIPAL_PANEL_USERNAME),
                                       Bundle.getMessage(Bundle.PRINCIPAL_PANEL_DEVICEID)};

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

            Sync4jPrincipal[] oldPrincipalArray = principals;
            principals = new Sync4jPrincipal[rows];

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
                    principals[i] = oldPrincipalArray[i];
                } else {
                    principals[i] = oldPrincipalArray[i + 1];
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
            return String.class;
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
            Sync4jPrincipal principal = principals[row];
            if (col == 0) {
                principal.setId(Long.parseLong((String)value));
            }
            if (col == 1) {
                principal.setUsername( (String)value);
            }
            if (col == 2) {
                principal.setDeviceId( (String)value);
            }
        }

        /**
         * Load values of recived principals into table
         * @param devices
         */
        public void loadPrincipals(Sync4jPrincipal[] principals) {

            int size = principals.length;
            this.principals = principals;
            rowData = new Object[size][columnNames.length];
            for (int i = 0; i < size; i++) {
                rowData[i][0] = String.valueOf(principals[i].getId());
                rowData[i][1] = (principals[i].getUsername());
                rowData[i][2] = (principals[i].getDeviceId());
            }
        }

    }

}
