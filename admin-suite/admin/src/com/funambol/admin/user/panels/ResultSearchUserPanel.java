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

package com.funambol.admin.user.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.ObjectsSortTools;
import com.funambol.admin.AdminException;
import com.funambol.admin.util.Log;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import java.text.MessageFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Create the panel used for display results of searching user.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a user are displayed
 * in the table through the model.
 *
 * @version $Id: ResultSearchUserPanel.java,v 1.9 2007-11-28 10:28:17 nichele Exp $
 **/
public class ResultSearchUserPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    /** model for result of search */
    private MyTableModel model = null;

    /** table for user data */
    private JTable table = null;

    /** parameter used if data in table change */
    private boolean changed = false;

    /** copy of user for restore old data discarding new edited values */
    private Sync4jUser userBackup = new Sync4jUser();

    /** user with modified parameter when editing */
    private Sync4jUser userModify = null;

    /** int used to remember row edited */
    private int oldRow = 0;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for search user result.
     */
    public ResultSearchUserPanel() {
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    /**
     * Loads the role into the table Model
     * @param roles Hashtable the roles to be loaded
     */
    public void setRoles(Hashtable roles) {
        ( (MyTableModel)table.getModel()).setRoles(roles);
    }

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        // create objects to display
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());

        model = new MyTableModel();        
        
        table = new JTable(model);
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(800, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        this.add(scrollpane, BorderLayout.CENTER);

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
                        ExplorerUtil.getSyncAdminController().getUsersController().
                            startEditUserProcess(
                                getUserSelected());
                    } catch (AdminException e) {
                        String description = Bundle.getMessage(Bundle.EDITING);
                        Log.error(
                            MessageFormat.format(
                                Bundle.getMessage(Bundle.ERROR_HANDLING_USER),
                                new String[] {description}
                            ),
                            e
                        );
                    }
                }
            }
        });

    }

    /**
     * <p>Create the model of the table.
     * The fields associated to a user are
     * managed in the model</p>
     *
     */
    private class MyTableModel extends AbstractTableModel {

        // -------------------------------------------------------- Private data
        /** user to display in table */
        private Sync4jUser[] users = null;

        /** table with the roles */
        private Hashtable rolesTable = null;

        /** properties of the table, contains the column names */
        String[] columnNames = new String[] {
                               Bundle.getMessage(Bundle.USER_PANEL_USERNAME),
                               Bundle.getMessage(Bundle.USER_PANEL_FIRSTNAME),
                               Bundle.getMessage(Bundle.USER_PANEL_LASTNAME),
                               Bundle.getMessage(Bundle.USER_PANEL_EMAIL),
                               Bundle.getMessage(Bundle.USER_PANEL_ROLES)};

        /** container of datas in the table  */
        Object[][] rowData = null;

        // -------------------------------------------------------- Constructors
        
        /**
         * Creates a new MyTableModel
         */
        public MyTableModel() {
        }

        
        /**
         * Creates a new MyTableModel
         * @param roles Hashtable the roles to load in the structure
         */
        public MyTableModel(Hashtable roles) {
            this.rolesTable = roles;
        }

        // ------------------------------------------------------ Public Methods
        /**
         * Returns rows number
         * @return int the number of rows
         */
        public int getRowCount() {
            if (rowData == null) {
                return 0;
            } else {
                return rowData.length;
            }
        }

        /**
         * load the roles into the model
         * @param roles Hashtable the roles to load
         */
        public void setRoles(Hashtable roles) {
            this.rolesTable = roles;
        }
        

        /**
         * Returns the roles
         */
        public Hashtable getRoles() {
            return this.rolesTable;
        }        

        /**
         * Returns columns number
         * @return int the number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * delete the row indexed by row param.
         * @param row int the line to be deleted.
         */
        public void deleteRow(int row) {
            int col = getColumnCount();
            int rows = getRowCount() - 1;
            boolean deleted = false;
            Object[][] oldRowData = rowData;
            rowData = new Object[rows][columnNames.length];

            Sync4jUser[] oldUsersArray = users;
            users = new Sync4jUser[rows];

            for (int i = 0; i < rows; i++) {
                if (i == row) {
                    deleted = true;
                }
                for (int y = 0; y < col; y++) {
                    if (!deleted) {
                        rowData[i][y] = oldRowData[i][y];
                        model.fireTableCellUpdated(i, y);
                    } else {
                        rowData[i][y] = oldRowData[i + 1][y];
                        model.fireTableCellUpdated(i, y);
                    }
                }

                if (!deleted) {
                    users[i] = oldUsersArray[i];
                } else {
                    users[i] = oldUsersArray[i + 1];
                }
            }
            model.fireTableDataChanged();

        }

        /**
         * Finds an returns the [row][column] object
         * @param row int the row of the table
         * @param column int the column of the table
         * @return Object the selected object
         */
        public Object getValueAt(int row, int column) {
            Object value = rowData[row][column];

            return value;
        }

        //-------------------------------------------------------
        // Overrides javax.swing.table.AbstractTableModel methods
        //-------------------------------------------------------
        /**
         * get the column name of the specified index
         * @param column int the index to find
         * @return String the column name
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
         * Set value of the user in the table and save a copy of the old values if edited
         * @param value user value to insert in the table
         * @param row selected row
         * @param col selected column
         */
        public void setValueAt(Object value, int row, int col) {
            // check if values are changed in table to correct double click
            if ( ( (String)rowData[row][col]).equals( (String)value) && !changed) {
                changed = false;
            } else {
                if (!changed) {
                    // save userBackup
                    userBackup.setUsername(users[row].getUsername());
                    userBackup.setFirstname(users[row].getFirstname());
                    userBackup.setLastname(users[row].getLastname());
                    userBackup.setEmail(users[row].getEmail());
                    userBackup.setPassword(users[row].getPassword());
                    userBackup.setRoles(users[row].getRoles());
                    oldRow = row;
                }

                rowData[row][col] = value;
                Sync4jUser user = users[row];
                if (col == 0) {
                    user.setUsername( (String)value);
                }
                if (col == 1) {
                    user.setFirstname( (String)value);
                }
                if (col == 2) {
                    user.setLastname( (String)value);
                }
                if (col == 3) {
                    user.setEmail( (String)value);
                }
                userModify = user;
                changed = true;
            }
        }

        /**
         * Load values of received users into table.
         * @param users the users to load
         */
        public void loadUsers(Sync4jUser[] users) {
            int size = users.length;
            this.users = users;
            rowData = new Object[size][columnNames.length];
            for (int i = 0; i < size; i++) {
                rowData[i][0] = (users[i].getUsername());
                rowData[i][1] = (users[i].getFirstname());
                rowData[i][2] = (users[i].getLastname());
                rowData[i][3] = (users[i].getEmail());
                rowData[i][4] = listRoles(users[i]);
            }
        }

        /**
         * List the roles of the user
         * @param user Sync4jUser the user
         * @return String the roles appended in a list form.
         */
        private String listRoles(Sync4jUser user) {
            StringBuffer listRoles = new StringBuffer();

            String[] roles = user.getRoles();
            int numRoles = roles.length;
            for (int i = 0; i < numRoles; i++) {
                if (i != 0) {
                    listRoles.append(", ");
                }
                listRoles.append(rolesTable.get(roles[i]));
            }
            return listRoles.toString();
        }

        /**
         * Load values of received user into table
         * @param user Sync4jUser the user to load
         * @param row int the row in which insert the user
         */
        public void setUser(Sync4jUser user, int row) {

            // modifico Sync4jUsers globale
            users[row].setUsername(user.getUsername());
            users[row].setFirstname(user.getFirstname());
            users[row].setLastname(user.getLastname());
            users[row].setEmail(user.getEmail());
            // lavoro sulla tabella
            model.setValueAt(user.getUsername(), row, 0);
            model.fireTableCellUpdated(row, 0);
            model.setValueAt(user.getFirstname(), row, 1);
            model.fireTableCellUpdated(row, 1);
            model.setValueAt(user.getLastname(), row, 2);
            model.fireTableCellUpdated(row, 2);
            model.setValueAt(user.getEmail(), row, 3);
            model.fireTableCellUpdated(row, 3);
        }

        /**
         * Get the roles description.
         * @param listId String the list of the roles
         * @return String the description of the roles passed in
         */
        private String getRolesDescription(String listId) {
            StringTokenizer listIdTokens = new StringTokenizer(listId, ",");
            String id = null;
            String description = null;
            StringBuffer descriptions = new StringBuffer();
            int i = 0;
            while (listIdTokens.hasMoreTokens()) {
                id = listIdTokens.nextToken();
                description = (String)rolesTable.get(id);
                if (i != 0) {
                    descriptions.append(",");
                }
                descriptions.append(description);
            }

            return descriptions.toString();
        }

    }

    /**
     * Load into the panel the users
     * @param users Sync4jUser[] the users to be loaded.
     */
    public void loadUsers(Sync4jUser[] users) throws AdminException {
    
         if (model.getRoles() == null) {
             boolean useCache = true;
             Hashtable roles = ExplorerUtil.getSyncAdminController()
                                           .getBusinessDelegate()
                                           .getRoles(useCache);  
             model.setRoles(roles);
         }
            
        ObjectsSortTools.sort("username", users);

        model.loadUsers(users);
        if (users != null && users.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);
        }
        table.updateUI();
    }

    /**
     * get the selected user.
     * @return Sync4jUser the user selected
     */
    public Sync4jUser getUserSelected() {
        int row = table.getSelectedRow();
        Sync4jUser userToDelete = model.users[row];
        return userToDelete;
    }

    /**
     * Shows a Notification Message.
     */
    public void showNotification() {

        Confirmation nd = new Confirmation(
            Bundle.getMessage(Bundle.USER_QUESTION_UPDATE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {

            boolean userUpdated = false;

            try {
                userUpdated = ExplorerUtil.getSyncAdminController().getUsersController().updateUser(
                    userModify);
            } catch (AdminException ex) {
                // ignore exception
            }

            if (userUpdated) {
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

        } else {
            model.setUser(userBackup, oldRow);
            changed = false;
        }

    }

    /**
     * Set the changed flag to false.
     */
    public void setNotChanged() {
        this.changed = false;
    }

    /**
     * Set the changed flag to true.
     */
    public void setChanged() {
        this.changed = true;
    }

    /**
     * Check if something is changed
     * @return boolean
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * Return the user to modify
     * @return Sync4jUser the user to modify
     */
    public Sync4jUser getUserModify() {
        return this.userModify;
    }

    /**
     * Returns table
     * @return table contained in the panel
     */
    public JTable getTable() {
        return this.table;
    }

    /**
     * Delete a row of the model.
     * @param row int the row to be deleted
     */
    public void deleteRowInTable(int row) {
        model.deleteRow(row);
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 230);
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

}
