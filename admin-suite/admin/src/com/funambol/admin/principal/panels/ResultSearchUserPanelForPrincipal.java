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

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.ObjectsSortTools;

/**
 * Create the panel used for display results of searching user.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a user (username, name, email) are display
 * in the table through the model.
 *
 * @version $Id: ResultSearchUserPanelForPrincipal.java,v 1.8 2007-11-28 10:28:17 nichele Exp $
 **/
public class ResultSearchUserPanelForPrincipal extends JPanel {

    // ------------------------------------------------------------ Private data

    /** model for result of search */
    private MyTableModel model = null;

    /** table for user data */
    private JTable table = null;

    /** int used to remember row edited */
    private int oldRow = 0;

    /** user name */
    private String userName = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for search user result.
     */
    public ResultSearchUserPanelForPrincipal() {

        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Pass users recived to model and update table to refresh values
     * @param users users array to load in the table
     */
    public void loadUsers(Sync4jUser[] users) {
        ObjectsSortTools.sort("username", users);
        model.loadUsers(users);

        if (users != null && users.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);

        }

        table.updateUI();
    }

    /**
     * Returns table
     * @return table contained in the panel
     */
    public JTable getTable() {
        return this.table;
    }

    /**
     * Returns user name
     * @return user name
     */
    public String getUserName() {
        return this.userName;
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }

    /**
     * Returns the number of selected rows.
     * @return the number of selected rows, 0 if no rows are selected
     */
    public int getNumRowsSelected() {
        return table.getSelectedRowCount();
    }

    /**
     * Returns the number of rows in this table's model.
     * @return the number of rows in this table's model.
     */
    public int getNumRows() {
        return table.getRowCount();
    }

    /**
     * Add <code>ListSelecctionListener</code> to the table's selection model
     * @param listener listener to add
     */
    public void addListSelectionListenerToTable(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
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
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        this.add(scrollpane, BorderLayout.CENTER);
    }

    /**
     *
     * <p>Create the model of the table.
     * The fields associated to a user (username, name, email) are
     * managed in the model</p>
     *
     */
    public class MyTableModel extends AbstractTableModel {

        // ------------------------------------------------------------ Private data

        /** users to display in table  */
        private Sync4jUser[] users = null;

        /** properties of the table, contains the column names */
        private String[] columnNames = new String[] {
                                       Bundle.getMessage(Bundle.USER_PANEL_USERNAME),
                                       Bundle.getMessage(Bundle.USER_PANEL_NAME),
                                       Bundle.getMessage(Bundle.USER_PANEL_EMAIL)};

        /** container of datas in the table  */
        private Object[][] rowData = null;

        // ------------------------------------------------------------ Constructors

        /**
         * void constructor
         */
        public MyTableModel() {
        }

        // ------------------------------------------------------------ Public Methods

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
         * Set not editable cells in table.
         * @param row selected row
         * @param col selected column
         * @return false because editing is not permitted
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        /**
         * Load values of recived users into table
         * @param devices
         */
        public void loadUsers(Sync4jUser[] users) {
            Log.debug("loadUsers");
            int size   = users.length;
            this.users = users;
            rowData    = new Object[size][columnNames.length];
            String       lastName   = null;
            String       firstName  = null;
            StringBuffer nameToShow = null;
            for (int i = 0; i < size; i++) {
                
                nameToShow = new StringBuffer();
                
                firstName  = users[i].getFirstname();
                lastName   = users[i].getLastname();
                
                if (firstName != null) {
                    nameToShow.append(firstName.trim());
                } 
                if (nameToShow.length() != 0) {
                    nameToShow.append(" ");
                }
                if (lastName != null) {
                    nameToShow.append(lastName.trim());
                }
                rowData[i][0] = (users[i].getUsername());
                rowData[i][1] = nameToShow.toString();
                rowData[i][2] = (users[i].getEmail());
            }
        }

    }

}
