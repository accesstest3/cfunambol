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

import java.text.MessageFormat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Create the panel used for display results of searching users.<br>
 * The panel includes a table and a model associated to table.
 * The model is used to manage table.
 * The fields associated to a device are displayed
 * in the table through the model.
 *
 * @version $Id: SearchUserPanel.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 *
 */
public class SearchUserPanel extends JPanel implements ActionListener, ListSelectionListener {

    // ------------------------------------------------------------ Private data

    /** global split panel */
    private JSplitPane splitPanel = null;

    /** panel for form */
    private FormSearchUserPanel formSearchUserPanel = null;

    /** panel for results of search */
    private JPanel resultSearchUserPanel = null;

    /** Panel containing all the buttons */
    private ButtonSearchUserPanel buttonSearchUserPanel = null;

    /** Panel for the search results.*/
    private ResultSearchUserPanel tableResultSearchUserPanel = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create panel that permit to manage user.<br>
     * The panel is composed of:<br>
     * a form to search user,<br>
     * a table for the result of the search,<br>
     * a button panel to handle the actions.
     */
    public SearchUserPanel() {
        this.setLayout(new BorderLayout());

        this.setName(Bundle.getMessage(Bundle.SEARCH_USER_PANEL_NAME));

        formSearchUserPanel = new FormSearchUserPanel();
        buttonSearchUserPanel = new ButtonSearchUserPanel();
        tableResultSearchUserPanel = new ResultSearchUserPanel();
        tableResultSearchUserPanel.addListSelectionListenerToTable(this);

        formSearchUserPanel.addFormActionListener(this);
        buttonSearchUserPanel.addButtonActionListener(this);

        resultSearchUserPanel = new JPanel();
        resultSearchUserPanel.setLayout(new BorderLayout());
        resultSearchUserPanel.add(tableResultSearchUserPanel, BorderLayout.CENTER);
        resultSearchUserPanel.add(buttonSearchUserPanel, BorderLayout.EAST);

        JScrollPane formPanel = new JScrollPane(formSearchUserPanel);
        formPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane resultPanel = new JScrollPane(resultSearchUserPanel);
        resultPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultPanel.setBorder(BorderFactory.createEtchedBorder());

        splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, formPanel, resultPanel);
        splitPanel.setDividerSize(0);
        splitPanel.setDividerLocation(210);
        splitPanel.setBorder(BorderFactory.createEmptyBorder());

        this.add(splitPanel, BorderLayout.CENTER);
    }

    // --------------------------------------------------------- public methods
    /**
     * Set the roles of the result table.
     * @param roles Hashtable the roles to be loaded
     */
    public void setRoles(Hashtable roles) {
        tableResultSearchUserPanel.setRoles(roles);
    }

    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed. Cases are:<br>
     * 1- modify : change user values with the edited one <br>
     * 2- cancel : delete selected row from the table and associated user from server<br>
     * 3- search : search user with form parameters and display results in table.<br>
     * @param event event occurs
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Log.debug("Process " + command + " command");

        String description = null;
        try {
            if (command.equals(Constants.ACTION_COMMAND_SAVE_USER)) {
                description = Bundle.getMessage(Bundle.UPDATING);
                if (tableResultSearchUserPanel.getTable().isEditing()) {
                    tableResultSearchUserPanel.setChanged();
                    tableResultSearchUserPanel.getTable().getCellEditor().stopCellEditing();
                }
                if (tableResultSearchUserPanel.isChanged()) {

                    boolean userUpdated = false;

                    userUpdated = ExplorerUtil
                                  .getSyncAdminController()
                                  .getUsersController()
                                  .updateUser(tableResultSearchUserPanel.getUserModify());

                    if (userUpdated) {
                        // updated values on server, so set table to not changed
                        tableResultSearchUserPanel.setNotChanged();
                    }
                }
            } else if (command.equals(Constants.ACTION_COMMAND_DELETE_USER)) {
                description = Bundle.getMessage(Bundle.DELETING);
                if (tableResultSearchUserPanel.getTable().getSelectedRow() == -1) {
                    NotifyDescriptor desc = new NotifyDescriptor.Message(
                        Bundle.getMessage(Bundle.NO_DATA_SELECTED));
                    DialogDisplayer.getDefault().notify(desc);
                } else {
                    Sync4jUser userToDelete = tableResultSearchUserPanel.getUserSelected();
                    boolean userDeleted = false;

                    userDeleted = ExplorerUtil
                                  .getSyncAdminController()
                                  .getUsersController()
                                  .deleteUser(userToDelete);

                    if (userDeleted) {
                        int row = tableResultSearchUserPanel.getTable().getSelectedRow();
                        tableResultSearchUserPanel.deleteRowInTable(row);
                    }
                }
            } else if (command.equals(Constants.ACTION_COMMAND_SEARCH_USER)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                Clause clause = formSearchUserPanel.getClause();

                ExplorerUtil.getSyncAdminController().getUsersController().searchUsers(clause);

            } else if (command.equals(Constants.ACTION_COMMAND_EDIT_USER)) {
                description = Bundle.getMessage(Bundle.EDITING);

                Sync4jUser userToEdit = tableResultSearchUserPanel.getUserSelected();
                ExplorerUtil.getSyncAdminController()
                            .getUsersController()
                            .startEditUserProcess(userToEdit);
            } else if (command.equals(Constants.ACTION_COMMAND_ADD_USER)) {
                description = Bundle.getMessage(Bundle.ADDING);
                ExplorerUtil.getSyncAdminController().getUsersController().startNewUserProcess("");

            }
        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_USER),
                    new String[] { description }
                ),
                e
            );
        }
    }

    /**
     * Load users in the panel
     * @param users Sync4jUser[] the users to be loaded.
     */
    public void loadUsers(Sync4jUser[] users) throws AdminException {
        tableResultSearchUserPanel.loadUsers(users);
        if (users != null && users.length > 0) {
            buttonSearchUserPanel.enableButtons(true);
        } else {
            buttonSearchUserPanel.enableButtons(false);
        }
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Enable buttons if value is changed
     * @param event ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent event) {

        int numRowsSelected = tableResultSearchUserPanel.getNumRowsSelected();
        if (numRowsSelected > 0) {
            buttonSearchUserPanel.enableButtons(true);
        } else {
            buttonSearchUserPanel.enableButtons(false);
        }
    }

}
