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

import com.funambol.admin.ui.PanelManager;
import java.text.MessageFormat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Generate a panel used to insert a principal.
 * The panel includes two panels that permitt to create a principal:
 * search user and search device.
 * User have to select one value from user's table and one value from device's table
 * to create a principal.
 *
 * @version $Id: CreatePrincipalPanel.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 **/
public class CreatePrincipalPanel extends JPanel implements ActionListener, ListSelectionListener {

    // ------------------------------------------------------------ Private data

    /** panel for title and main bottom */
    private JSplitPane splitPanel = null;

    /** panel for user search and device search called main */
    private JSplitPane splitPanelMain = null;

    /** panel for main and bottom */
    private JSplitPane panelMainBottom = null;

    /** panel for user's form with search button */
    private FormSearchUserPanelForPrincipal formSearchUserPanelForPrincipal = null;

    /** panel for table with user's search results */
    private ResultSearchUserPanelForPrincipal resultSearchUserPanel = null;

    /** panel for device's form with search button */
    private FormSearchDevicePanelForPrincipal formSearchDevicePanelForPrincipal = null;

    /** panel for table with device's search results */
    private ResultSearchDevicePanelForPrincipal resultSearchDevicePanel = null;

    /** label for the panel's name */
    private JLabel panelName = new JLabel();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** button to insert a new principal */
    private JButton buttonInsert = new JButton();
    
    /** button to cancel the action */
    private JButton buttonCancel = new JButton();

    // ------------------------------------------------------------ Constructors

    /**
     * Create panel that permitt to create principal.<br>
     * The panel is composed of:<br>
     * - a title <br>
     * - a form to search user and a table for the result of the search<br>
     * - a form to search device and a table for the result of the search<br>
     * - a insert button to insert principal.
     */
    public CreatePrincipalPanel() {
        // set global parameters and properties of the panel
        this.setLayout(new BorderLayout());
        this.setName(Bundle.getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));

        // title panel
        JPanel titlePanel = new JPanel();
        titledBorder1 = new TitledBorder("");
        titlePanel.setLayout(null);

        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);
        titlePanel.add(panelName, null);
        //end title panel

        // left panel ( for search user )
        formSearchUserPanelForPrincipal = new FormSearchUserPanelForPrincipal();
        // add this panel as listener of other's panel button
        formSearchUserPanelForPrincipal.addFormUserActionListener(this);

        resultSearchUserPanel = new ResultSearchUserPanelForPrincipal();
        resultSearchUserPanel.addListSelectionListenerToTable(this);

        JSplitPane leftPanel = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, false, formSearchUserPanelForPrincipal,
            resultSearchUserPanel);
        leftPanel.setDividerSize(0);
        leftPanel.setDividerLocation(200);
        leftPanel.setPreferredSize(new Dimension(400, 350));
        //end left panel

        //right panel ( for device search )
        formSearchDevicePanelForPrincipal = new FormSearchDevicePanelForPrincipal();
        // add this panel as listener of other's panel button
        formSearchDevicePanelForPrincipal.addFormDeviceActionListener(this);

        formSearchDevicePanelForPrincipal.setPreferredSize(new Dimension(450, 200));
        resultSearchDevicePanel = new ResultSearchDevicePanelForPrincipal();
        resultSearchDevicePanel.setPreferredSize(new Dimension(450, 500));
        resultSearchDevicePanel.addListSelectionListenerToTable(this);

        JSplitPane rightPanel = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, false, formSearchDevicePanelForPrincipal,
            resultSearchDevicePanel);

        rightPanel.setDividerSize(0);
        rightPanel.setDividerLocation(200);
        rightPanel.setPreferredSize(new Dimension(450, 350));
        //end right panel

        // bottom panel ( for button insert )
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);

        buttonPanel.setFont(GuiFactory.defaultFont);

        buttonInsert.setBounds(new Rectangle(247, 3, 100, 28));
        buttonInsert.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD_PRINCIPAL));
        buttonInsert.setActionCommand(Constants.ACTION_COMMAND_ADD_PRINCIPAL);
        buttonInsert.setFont(GuiFactory.defaultFont);
        
        buttonCancel.setBounds(new Rectangle(353, 3, 100, 28));
        buttonCancel.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
        buttonCancel.setFont(GuiFactory.defaultFont);
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelAction();
            }
        });

        buttonPanel.add(buttonInsert, null);
        buttonPanel.add(buttonCancel, null);
        buttonInsert.addActionListener(this);
        buttonPanel.setPreferredSize(new Dimension(700, 35));
        buttonInsert.setEnabled(false);
        //end bottom panel

        // create panel with search user and search device : called main
        splitPanelMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, leftPanel, rightPanel);
        splitPanelMain.setDividerSize(0);
        splitPanelMain.setDividerLocation(350);
        splitPanelMain.setBorder(BorderFactory.createEmptyBorder());
        splitPanelMain.setPreferredSize(new Dimension(700, 420));

        // create panel with main and bottom
        panelMainBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, splitPanelMain,
                                         buttonPanel);
        panelMainBottom.setDividerSize(0);
        panelMainBottom.setDividerLocation(350);
        panelMainBottom.setBorder(BorderFactory.createEmptyBorder());
        panelMainBottom.setPreferredSize(new Dimension(700, 420));

        // create panel with title and main-bottom
        splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, titlePanel, panelMainBottom);
        splitPanel.setDividerSize(0);
        splitPanel.setDividerLocation(40);
        splitPanel.setBorder(BorderFactory.createEmptyBorder());
        splitPanel.setPreferredSize(new Dimension(700, 420));

        // create final panel with title-main-bottom
        JScrollPane finalPanel = new JScrollPane(splitPanel);
        finalPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        finalPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        finalPanel.setBorder(BorderFactory.createEmptyBorder());

        this.add(finalPanel, BorderLayout.CENTER);

    }
    
    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    } 

    // ---------------------------------------------------------- Public methods

    /**
     * Enables insert button if values is selected
     */
    public void checkNumberOfDevicesAndUsers() {
        int numDevices = resultSearchDevicePanel.getNumRows();
        int numUsers = resultSearchUserPanel.getNumRows();

        if (numDevices > 0 && numUsers > 0) {
            buttonInsert.setEnabled(true);
        } else {
            buttonInsert.setEnabled(false);
        }
    }

    /**
     * Loads Users in the table
     * @param users Sync4jUser[]
     */
    public void loadUsers(Sync4jUser[] users) {
        resultSearchUserPanel.loadUsers(users);
    }

    /**
     * Loads devices in the table
     * @param devices Sync4jDevice[]
     */
    public void loadDevices(Sync4jDevice[] devices) {
        resultSearchDevicePanel.loadDevices(devices);
    }

    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed. Cases are:<br>
     * 1- insert : insert principal with the selected values <br>
     * 2- search user : search user with form parameters and
     *  display results in user result table.<br>
     * 3- search device : search device with form parameters and
     *  display results in device result table.<br>
     * @param event
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Log.debug("Process " + command + " command");

        String description = null;

        try{
            // insert action
            if (command.equals(Constants.ACTION_COMMAND_ADD_PRINCIPAL)) {
                description = Bundle.getMessage(Bundle.ADDING);

                // check if one row in result search user and
                // one in result search device are selected to make a correct insert
                if (resultSearchUserPanel.getTable().getSelectedRow() == -1 ||
                    resultSearchDevicePanel.getTable().getSelectedRow() == -1) {
                    // error notification
                    NotifyDescriptor desc = new NotifyDescriptor.Message(
                        Bundle.getMessage(Bundle.NO_DATA_SELECTED));
                    DialogDisplayer.getDefault().notify(desc);
                } else {
                    // ok, row selected

                    // get parameters for insert from tables
                    JTable userTable = resultSearchUserPanel.getTable();
                    String username = (String)userTable.getValueAt(userTable.getSelectedRow(), 0);
                    JTable deviceTable = resultSearchDevicePanel.getTable();
                    String deviceID = (String)deviceTable.getValueAt(deviceTable.getSelectedRow(),
                        0);
                    // create principal with parameters
                    Sync4jPrincipal principal = Sync4jPrincipal.createPrincipal(
                                                      username, deviceID);
                    ExplorerUtil.getSyncAdminController()
                                .getPrincipalsController()
                                .insertNewPrincipal(principal);
                }
            } else if (command.equals(Constants.ACTION_COMMAND_SEARCH_USER)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                // create clause
                Clause clause = formSearchUserPanelForPrincipal.getClause();

                ExplorerUtil.getSyncAdminController()
                            .getPrincipalsController()
                            .searchUsersForPrincipal(clause);
            } else if (command.equals(Constants.ACTION_COMMAND_SEARCH_DEVICE)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                // create clause
                Clause clause = formSearchDevicePanelForPrincipal.getClause();

                ExplorerUtil.getSyncAdminController()
                            .getPrincipalsController()
                            .searchDevicesForPrincipal(clause);
            }
        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_PRINCIPAL),
                    new String[] {description}
                ),
                e
            );
        }
    }

    /**
     * Enables insert button if values is selected
     * @param event ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent event) {
        int numDevicesSelected = resultSearchDevicePanel.getNumRowsSelected();
        int numUsersSelected = resultSearchUserPanel.getNumRowsSelected();

        if (numDevicesSelected > 0 && numUsersSelected > 0) {
            buttonInsert.setEnabled(true);
        } else {
            buttonInsert.setEnabled(false);
        }
    }

}
