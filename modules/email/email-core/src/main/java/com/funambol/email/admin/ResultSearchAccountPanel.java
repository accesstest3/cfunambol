/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.JCheckBox;
import com.funambol.email.model.MailServerAccount;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;
import java.awt.event.MouseListener;


/**
 * Panel used for displaying results of an account search.
 *
 * @version $Id: ResultSearchAccountPanel.java,v 1.2 2008-07-17 09:55:50 testa Exp $
 **/
public class ResultSearchAccountPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    /** model for result of search */
    private AccountTableModel model;

    /** table for account data */
    private JTable table;

    /** parameter used if data in table change */
    private boolean changed = false;

    /** accounts */
    private MailServerAccount[] accounts;

    /** selected account */
    private MailServerAccount selectedAccount;

    /** table index of the selected account */
    private int selectedRow = -1;

    /** buttons for handling the account list (add, edit, cancel) */
    private JButton jBAdd = new JButton();
    private JButton jBEdit = new JButton();
    private JButton jBDelete = new JButton();
    private JButton jBShowCache = new JButton();

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for search user result.
     */
    public ResultSearchAccountPanel() {
        try {
            initGui();
        } catch (Exception e) {
            Log.error("Error creating Result Search Account Panel " + getClass().getName(), e);
        }
    }

    /**
     * Initializes visual elements.
     *
     * @throws Exception if error occures during creation of the panel
     */
    private void initGui() throws Exception {

        setLayout(new BorderLayout());

        //
        // buttons panel for handling table data (add, delete, edit buttons)
        //
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);
        add(buttonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(jBAdd);
        buttonsPanel.add(jBEdit);
        buttonsPanel.add(jBDelete);
        buttonsPanel.add(jBShowCache);

        jBAdd.setBounds      (new Rectangle(4, 4,  100, 20));
        jBEdit.setBounds     (new Rectangle(4, 34, 100, 20));
        jBDelete.setBounds   (new Rectangle(4, 64, 100, 20));
        jBShowCache.setBounds(new Rectangle(4, 94, 100, 20));

        jBAdd.setText("Add");
        jBEdit.setText("Edit");
        jBDelete.setText("Delete");
        jBShowCache.setText("Cache");

        jBAdd.setActionCommand(Constants.ACTION_COMMAND_ADD_ACCOUNT);
        jBEdit.setActionCommand(Constants.ACTION_COMMAND_EDIT_ACCOUNT);
        jBDelete.setActionCommand(Constants.ACTION_COMMAND_DELETE_ACCOUNT);
        jBShowCache.setActionCommand(Constants.ACTION_COMMAND_SHOW_CACHE);

        enableButtons(false);

        //
        // model for table data
        //
        model = new AccountTableModel();
        table = new JTable(model);

        //
        // accounts table
        //
        setBorder(BorderFactory.createEmptyBorder());

        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 400));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);

        //
        // Each checkbox appears like if it were disabled.
        //
        ((JCheckBox)table.getDefaultRenderer(Boolean.class)).setEnabled(false);


        add(scrollpane, BorderLayout.WEST);

        ListSelectionModel rowSM = table.getSelectionModel();

        //
        // Selected an account from the search results.
        //
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (event.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel)event.getSource();
                if (!lsm.isSelectionEmpty()) {

                    selectedRow = lsm.getMinSelectionIndex();
                    selectedAccount = accounts[selectedRow];
                }
            }
        });

        rowSM.clearSelection();
    }

    /**
     * Enables/disables the edit and delete buttons.
     * @param enabled if <code>true</code> buttons are enebled, disabled
     * otherwise
     */
    private void enableButtons(boolean enabled){
        jBEdit.setEnabled(enabled);
        jBDelete.setEnabled(enabled);
        jBShowCache.setEnabled(enabled);
    }

    /**
     * Model of the table used to display account search results.
     */
    private class AccountTableModel extends AbstractTableModel {

        // ------------------------------------------------------------ Private data

        /** user to be displaied in the table */
        private MailServerAccount[] accounts = null;

        /** column names */
        String[] columnNames = new String[] {
                               "Username",
                               "Login",
                               "Address",
                               "Activation",
                               "Max email",
                               "Soft-Delete on server",
                               "Description",
                               "Protocol"};

        /**
         * Container of data in the table.
         * <p/>
         * Must be not null at the time of creation.
         */
        Object[][] rowData = new Object[0][columnNames.length];

        // ---------------------------------------------------------- Public methods

        /**
         * Load values of received users into table.
         * @param users the users to load
         */
        public void loadAccounts(MailServerAccount[] accounts) {

            int size = accounts.length;

            if (size != 0){
                this.accounts = accounts;
                rowData = new Object[size][columnNames.length];
                for (int i = 0; i < size; i++) {
                    setValueAt(accounts[i].getUsername(), i, 0);
                    setValueAt(accounts[i].getMsLogin(), i, 1);
                    setValueAt(accounts[i].getMsAddress(), i, 2);

                    //setValueAt(new Boolean(accounts[i].getActivation()), i, 3);
                    setValueAt(new Boolean(accounts[i].getActive()), i, 3);

                    setValueAt(new Integer(accounts[i].getMaxEmailNumber()), i, 4);
                    setValueAt(new Boolean(accounts[i].getMailServer().getIsSoftDelete()), i, 5);
                    setValueAt(accounts[i].getMailServer().getDescription(), i, 6);
                    setValueAt(accounts[i].getMailServer().getProtocol(), i, 7);

                }
                enableButtons(true);

            } else {
                rowData = new Object[0][columnNames.length];
                enableButtons(false);
            }
            table.updateUI();
       }

        /**
         * Remove selected row from the accounts displaied by the table.
         *
         * @paran selectedRow index of the row to be removed
         */
        public void deleteRow(int selectedRow){

            int size = accounts.length;
            
            if (size > 0){
                
                // deletes account from this model's account list
                MailServerAccount[] tmp = new MailServerAccount[size - 1];
                int j = 0;
                for (int i = 0; i < size; i++) {
                    if (i == selectedRow){
                        continue;
                    }
                    tmp[j] = accounts[j];
                    j++;
                }

                // deletes account from the displaied data
                Object[][] tmpDisplaied = new Object[rowData.length - 1][];
                j = 0;
                for (int i = 0; i < rowData.length; i++) {
                    if (i == selectedRow){
                        continue;
                    }
                    tmpDisplaied[j] = rowData[j];
                    j++;
                }

                rowData = tmpDisplaied;
                accounts = tmp;

                if (accounts.length == 0){
                    enableButtons(false);
                }
            }
        }

        /**
         * Update row of the accounts displaied by the table.
         *
         * @paran selectedRow index of the row to be updated
         * @param account updated account
         */
        public void updateRow(int selectedRow, MailServerAccount account){

            setValueAt(account.getUsername(), selectedRow, 0);
            setValueAt(account.getMsLogin(), selectedRow, 1);
            setValueAt(account.getMsAddress(), selectedRow, 2);

            //setValueAt(new Boolean(account.getActivation()), selectedRow, 3);
            setValueAt(new Boolean(account.getActive()), selectedRow, 3);

            setValueAt(new Integer(account.getMaxEmailNumber()), selectedRow, 4);
            setValueAt(new Boolean(account.getMailServer().getIsSoftDelete()), selectedRow, 5);
            setValueAt(account.getMailServer().getDescription(), selectedRow, 6);
            setValueAt(account.getMailServer().getProtocol(), selectedRow, 7);

            for (int i = 0; i < rowData.length; i++) {
                if (accounts[i].getId() == account.getId()){
                    accounts[i] = account;
                }
            }

            updateUI();
        }

        // -------------------------------------------------------- Override methods

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return rowData.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /**
         * This method is used also by the JTable constructor in order to fill
         * the table with the initial data content, if any.
         */
        public Object getValueAt(int row, int col) {
            return rowData[row][col];
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
        }

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Load into the panel the accounts
     * @param accounts MailServerAccount[] the accounts to be loaded.
     */
    public void loadAccounts(MailServerAccount[] accounts) throws AdminException {

        // TODO sort accounts

        this.accounts = accounts;

        model.loadAccounts(accounts);
        if (accounts != null && accounts.length > 0) {
            // select first row
            table.setRowSelectionInterval(0, 0);
        }
        table.updateUI();
    }

    /**
     * Gets the selected account.
     *
     * @return the account selected
     */
    public MailServerAccount getSelectedAccount() {
        int row = table.getSelectedRow();        
        if (row == -1) {
            return null;
        }        
        MailServerAccount accountToDelete = model.accounts[row];            
        return accountToDelete;
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
     * Returns table
     * @return table contained in the panel
     */
    public JTable getTable() {
        return this.table;
    }

    /**
     * Delete a row of the model.
     */
    public void deleteRowInTable() {
        AccountTableModel model = (AccountTableModel)table.getModel();
        model.deleteRow(selectedRow);
        table.getSelectionModel().clearSelection();        
        table.updateUI();
    }

    /**
     * Updates a row of the model.
     *
     * @param account MailServerAccount the row to be updated
     */
    public void updateRowInTable(MailServerAccount account) {
        AccountTableModel model = (AccountTableModel)table.getModel();
        model.updateRow(selectedRow, account);
        table.updateUI();
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 430);
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

    public void addActionListener(ActionListener actionListener){
        jBAdd.addActionListener(actionListener);
        jBEdit.addActionListener(actionListener);
        jBDelete.addActionListener(actionListener);
        jBShowCache.addActionListener(actionListener);
    }

    /**
     * Sets the action for the mouse click event over a row.
     * @param actionListener
     */
    public void setRowMouseAction(MouseListener mouseListener){
        table.addMouseListener(mouseListener);
    }
}
