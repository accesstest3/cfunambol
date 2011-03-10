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
package com.funambol.email.admin.user;

import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServerAccount;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.tools.beans.BeanException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.funambol.email.admin.Step;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.ObjectsSortTools;
import com.funambol.admin.AdminException;
import com.funambol.admin.util.Log;

import java.text.MessageFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.apache.commons.lang.text.StrBuilder;

/**
 * This class represent the panel used for display results of a user search.
 *
 * @version $Id: ResultSearchUserPanel.java,v 1.2 2008-07-17 09:55:50 testa Exp $
 *
 **/
public class ResultSearchUserPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    /** users that comes from a search */
    private Sync4jUser[] users;

    /** model for the search result table */
    private UserTableModel model;

    /** table that display search result */
    private JTable table;

    /**
     * Object that holds the "next step" logic (that is: how the process using
     * this panel goes to the next process step)
     */
    private Step step;

    // -------------------------------------------------------------- Properties

    /** selected user */
    private Sync4jUser selectedUser;

    /**
     * Gets the selectedUser property.
     *
     * @return selected user
     */
    public Sync4jUser getSelectedUser(){
        return selectedUser;
    }

    /** data access object */
    private WSDAO WSDao;

    /** Sets the WSDao */
    public void setWSDao(WSDAO WSDao) {
        this.WSDao = WSDao;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for displaying search user result.
     *
     * @param step object that holds the "next step" logic
     */
    public ResultSearchUserPanel(Step step) {
        try {
            this.step = step;
            init();
        } catch (Exception e) {
            Log.error("Error creating Reasult Users Panel " + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Loads users to be displaied by the table.
     *
     * @param users users to be displaied
     */
    public void loadUsers(Sync4jUser[] users){
        this.users = users;
        this.model.loadUsers(this.users);
    }

    /**
     * Resets the search result table (that is: no record has to be selected).
     */
    public void resetResultTableSelection() {
        
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.clearSelection();
    }

    // --------------------------------------------------------- Private methods
    /**
     * Set up graphic elements for this panel.
     *
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        // create objects to display
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());

        // create a model for the user table and pass it to the JTable object
        model = new UserTableModel();
        table = new JTable(model);

        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollpane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(800, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        this.add(scrollpane, BorderLayout.CENTER);

        //
        // Select user.
        //
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel rowSM = table.getSelectionModel();

        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (event.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel)event.getSource();
                
                if (lsm.isSelectionEmpty()) {

                    selectedUser = null;
                } else {
                    
                    int selectedRow = lsm.getMinSelectionIndex();
                    selectedUser = users[selectedRow];                    
                }
            }
        });

        rowSM.clearSelection();

        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent event) {

                if (event.getClickCount() < 2) {
                    return;
                }

                //
                // If the selected user is already associated to an account
                // then insertion process can't go on.
                //

                String userName = selectedUser.getUsername();
                String value[] = new String[]{userName};
                WhereClause wc = new WhereClause("username", value, WhereClause.OPT_EQ, true);
                MailServerAccount[] tmp = null;
                try {
                    tmp = WSDao.getAccounts(wc);
                } catch (Exception e) {
                }

                if (tmp.length > 0){

                    StringBuilder sb = new StringBuilder("The user ");
                    sb.append(userName).append(" is already associated to an account");
                    Object[] options = {"OK"};
                    JOptionPane.showOptionDialog(null, sb.toString(), "Warning",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);

                } else {
                    //
                    // Go to next step.
                    //
                    step.goToNextStep();
                }
            }
        });

    }

    // ----------------------------------------------------------- Inner classes

    /**
     * Model that is bound to the result table.
     */
    private class UserTableModel extends AbstractTableModel {

        // ------------------------------------------------------------ Private data

        /** column names */
        String[] columnNames = new String[] {"Username", "First Name", "Last Name", "E-mail"};

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
        public void loadUsers(Sync4jUser[] users) {
            
            if (users == null) {
                return;
            }
            
            int size = users.length;
            
            if (size != 0) {
                rowData = new Object[size][columnNames.length];
                for (int i = 0; i < size; i++) {
                    setValueAt(users[i].getUsername(), i, 0);
                    setValueAt(users[i].getFirstname(), i, 1);
                    setValueAt(users[i].getLastname(), i, 2);
                    setValueAt(users[i].getEmail(), i, 3);
                }
            } else {
                rowData = new Object[0][columnNames.length];
            }
            table.updateUI();
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
        }

    }

}
