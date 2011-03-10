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

package com.funambol.admin.settings.panels;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import org.openide.*;

import com.funambol.framework.config.*;

import com.funambol.admin.AdminException;
import com.funambol.admin.settings.nodes.LoggerSettingsNode;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Generate a panel used to edit a LoggerConfiguration object.
 *
 * @version $Id: EditLoggerPanel.java,v 1.16 2008-08-08 12:28:16 luigiafassina Exp $
 **/
public class EditLoggerPanel extends JPanel implements ActionListener {

    // --------------------------------------------------------------- Constants

    public static final String[] LEVELS = {
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_OFF),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_FATAL),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_ERROR),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_WARN),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_INFO),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_DEBUG),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_TRACE),
        Bundle.getMessage(Bundle.LABEL_LOGLEVEL_ALL)
    };


    // ------------------------------------------------------------ Private data

    private static final String ACTION_ADD_USER    = "ACTION_ADD_USER";
    private static final String ACTION_REMOVE_USER = "ACTION_REMOVE_USER";


    /** label for the panel's name */
    private JLabel panelNameLabel = new JLabel();

    /** label for the name */
    private JLabel nameLabel = new JLabel();

    /** the name */
    private JLabel nameLabel2 = new JLabel();

    /** label for the "same as" */
    private JLabel inheritLabel = new JLabel();

    /** check for making the logger inherit from its parent **/
    private JCheckBox inheritCheck = new JCheckBox();

    /** label for the level */
    private JLabel levelLabel = new JLabel();

    /** Combo for the level */
    private JComboBox levelCombo = new JComboBox(LEVELS);

    /** label for the appenders */
    private JLabel appendersLabel = new JLabel();

    /** JList for the available appenders */
    private JList appendersList = null;

    /** label for the "user to Level.ALL" */
    private JLabel usersWithLevelALLLabel = new JLabel();

    /** JTable for the users list */
    private JTable usersTable = null;

    /** Button to add a new user */
    private JButton addUserButton = null;

    /** Button to remove the selected user */
    private JButton removeUserButton = null;

    /** Confirm button*/
    private JButton confirmButton = new JButton();

    private LoggerSettingsNode loggerNode;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for the given LoggerConfiguration object.
     */
    public EditLoggerPanel() {

        try {
            init();
            loggerNode = null;
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }

    // ---------------------------------------------------------- Public methods

   /**
     * Loads the LoggerConfiguration values into the panel.
     *
     * @param loggerNode the logger settings node
     */
    public void loadSettings(LoggerSettingsNode loggerNode) {
        this.loggerNode = loggerNode;

        LoggerConfiguration config = loggerNode.getConfig();

        boolean isInherit = config.isInherit();

        String name = config.getName().trim();

        nameLabel2.setText(name);
        inheritCheck.setSelected(isInherit);
        levelCombo.setSelectedItem(config.getLevel().toUpperCase());

        List configuredAppenders = config.getAppenders();
        selectAppendersInJList(configuredAppenders);

        List usersWithALLLevel = config.getUsersWithLevelALL();
        if (usersWithALLLevel == null) {
            usersWithALLLevel = new ArrayList();
        }
        Collections.sort(usersWithALLLevel);
        ((UsersTableModel)usersTable.getModel()).setUsers(usersWithALLLevel);


        //
        // Set inheritance label and status. If the logger name has not parent,
        // the label will be "system configuration", otherwise it will be the
        // closest oparent.
        //
        String parentName = null;
        int p = name.lastIndexOf('.');
        if (p > 0) {
            parentName = name.substring(0, p);
        }

        if (parentName != null) {
            inheritLabel.setVisible(true);
            inheritCheck.setVisible(true);
            inheritLabel.setText( Bundle.getMessage(Bundle.LABEL_LOGGER_INHERIT)
                                + ' '
                                + parentName
                                + " :"
                                );

            setInheritanceStatus(isInherit);
        } else {
            inheritLabel.setVisible(false);
            inheritCheck.setVisible(false);
            setInheritanceStatus(false);
        }
    }

    /**
     * Returns the preferred dimension of this panel
     * @return the preferred dimension of this panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 490);
    }

    /**
     * Sets the list of the available appenders
     */
    public void setAvailableAppenders(java.util.List availableAppenders) {
        ((AppendersListModel)appendersList.getModel()).
                                           setAvailableAppenders(availableAppenders);
        appendersList.updateUI();
    }

    public void actionPerformed(ActionEvent e) {
        if (ACTION_ADD_USER.equals(e.getActionCommand())) {

            //add a new item into incoming table
            int poz = ((UsersTableModel)usersTable.getModel()).addItem();
            //select the new row and scroll to the line
            usersTable.getSelectionModel().addSelectionInterval(poz,poz);
            int rowPoz = usersTable.getRowHeight()*(poz+1);
            usersTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));

        } else if(ACTION_REMOVE_USER.equals(e.getActionCommand())) {

            //remove seleced row from incoming table
            int selectedIndex = usersTable.getSelectedRow();
            if (usersTable.isEditing()) {
                usersTable.getCellEditor().stopCellEditing();
            }
            ((UsersTableModel)usersTable.getModel()).removeItem(selectedIndex);

            int count = usersTable.getRowCount();
            if (count > 0) {
                usersTable.setRowSelectionInterval(0, 0);
            }

        }
    }

    // ---------------------------------------------------------- Private method
    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel.
     */
    private void init() throws Exception {

        final int h  = 18;
        final int x1 = 15;
        final int x2 = 160;
        final int w1 = 130;
        final int w2 = 200;

        int y  = 40;
        int dy = 25;

        setLayout(null);
        setName(Bundle.getMessage(Bundle.EDIT_LOGGER_PANEL_NAME));

        panelNameLabel.setText(Bundle.getMessage(Bundle.EDIT_LOGGER_PANEL_NAME));
        panelNameLabel.setBounds(new Rectangle(14, 5, 216, 28));
        panelNameLabel.setAlignmentX(SwingConstants.CENTER);
        panelNameLabel.setBorder(new TitledBorder(""));

        nameLabel.setText(Bundle.getMessage(Bundle.LABEL_LOGGER_NAME) + " :");
        nameLabel.setBounds(new Rectangle(x1, y, w1, h));
        nameLabel2.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;
        inheritLabel.setText(Bundle.getMessage(Bundle.LABEL_LOGGER_INHERIT) + " :");
        inheritLabel.setBounds(new Rectangle(x1, y, w1, h));
        inheritCheck.setBounds(new Rectangle(x2, y, w2, h));

        inheritCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInheritanceStatus(inheritCheck.isSelected());
            }
        });

        y += dy;
        levelLabel.setText(Bundle.getMessage(Bundle.LABEL_LOGGER_LEVEL) + " :");
        levelLabel.setBounds(new Rectangle(x1, y, w1, h));
        levelCombo.setBounds(new Rectangle(x2, y, w2, h));
        levelCombo.setFont(GuiFactory.defaultFont);

        y += dy;
        appendersLabel.setText(Bundle.getMessage(Bundle.LABEL_APPENDERS) + " :");
        appendersLabel.setBounds(new Rectangle(x1, y, w1, h));

        appendersList = new JList(new AppendersListModel());
        appendersList.setFont(GuiFactory.defaultFont);
        JScrollPane appendersScrollPane = new JScrollPane(appendersList);
        appendersScrollPane.setBounds(new Rectangle(x2, y, w2, h * 5));
        appendersScrollPane.setAutoscrolls(true);
        y += h * 4;

        y += dy;
        usersWithLevelALLLabel.setText(Bundle.getMessage(Bundle.LABEL_USERS_WITH_LEVEL_ALL) + " :");
        usersWithLevelALLLabel.setBounds(new Rectangle(x1, y, w1, h));

        //panel for users with Level.ALL
        JPanel usersPanel=new JPanel();
        usersPanel.setLayout(null);
        usersPanel.setBounds(new Rectangle(x1, y, w1 + w2 + (x2 - (x1 + w1)), h * 12));
        TitledBorder tb1=new TitledBorder(Bundle.getMessage(Bundle.LABEL_USERS_WITH_LEVEL_ALL));
        tb1.setTitleFont(GuiFactory.defaultTableHeaderFont);
        usersPanel.setBorder(tb1);

        usersTable = new JTable(new UsersTableModel((java.util.List)null,
                                                    new String[] {"User name"}));
        usersTable.getTableHeader().addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                sortTable(e);
            }
            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            private void sortTable(MouseEvent e) {
                JTable table = ((JTableHeader)e.getSource()).getTable();
                ((UsersTableModel)table.getModel()).sortUsers();
            }
        });

        usersTable.setVisible(true);
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        usersScrollPane.setBounds(new Rectangle(17, 40, w1 + w2 - 20, h * 9));
        usersScrollPane.setAutoscrolls(true);

        usersPanel.add(usersScrollPane);
        //
        // put add buttons
        //
        addUserButton = new JButton("+");
        addUserButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addUserButton.setMargin(new Insets(0,0,0,0));
        addUserButton.setBounds(260, 20, 18, 18);
        addUserButton.addActionListener(this);
        addUserButton.setActionCommand(ACTION_ADD_USER);
        usersPanel.add(addUserButton);
        //
        // put remove buttons
        //
        removeUserButton = new JButton("-");
        removeUserButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        removeUserButton.setMargin(new Insets(0,0,0,0));
        removeUserButton.setBounds(280, 20, 18, 18);
        removeUserButton.addActionListener(this);
        removeUserButton.setActionCommand(ACTION_REMOVE_USER);
        usersPanel.add(removeUserButton);

        y += h * 12;

        y += dy;
        confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));
        confirmButton.setBounds(new Rectangle(276, y, 60, 25));

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (usersTable.isEditing()) {
                    usersTable.getCellEditor().stopCellEditing();
                }
                setLoggerConfiguration();
            }
        });

        add(panelNameLabel     , null);
        add(nameLabel          , null);
        add(nameLabel2         , null);
        add(inheritLabel       , null);
        add(inheritCheck       , null);
        add(levelLabel         , null);
        add(levelCombo         , null);
        add(appendersLabel     , null);
        add(appendersScrollPane, null);
        add(usersPanel         , null);
        add(confirmButton      , null);

        GuiFactory.setDefaultFont(this);
        panelNameLabel.setFont(GuiFactory.titlePanelFont);

    }

    /**
     * Selects in the appenders list the ones given
     * @param appenders List
     */
    private void selectAppendersInJList(List appenders) {
        int[] indicesToSelect = ( (AppendersListModel)appendersList.getModel()).getIndices(appenders);
        appendersList.setSelectedIndices(indicesToSelect);
    }

    /**
     * Enable/disable all input accordingly to the given inheritance
     *
     * @param inherit does the logger inherit from its parent
     */
    private void setInheritanceStatus(boolean inherit) {
        boolean enabled = !inherit;

        levelCombo.setEnabled(enabled);
        appendersList.setEnabled(enabled);
        usersTable.setEnabled(enabled);
        usersTable.getTableHeader().setEnabled(enabled);
        addUserButton.setEnabled(enabled);
        removeUserButton.setEnabled(enabled);
    }

    /**
     * Validates the inputs and throws an Exception in case of any invalid data.
     *
     * @throws IllegalArgumentException in case of invalid input
     */
    private void validateInput()
    throws IllegalArgumentException {
    }

    /**
     * Saves the inserted value in the node's LoggerConfiguration.
     */
    private void getValues() {
        LoggerConfiguration config = loggerNode.getConfig();

        config.setInherit(inheritCheck.isSelected());
        config.setLevel((String)levelCombo.getSelectedItem());

        Object[] selectedAppenders = appendersList.getSelectedValues();
        List appendersList = new ArrayList();
        for (int i=0; i<selectedAppenders.length; i++) {
            appendersList.add(selectedAppenders[i]);
        }
        config.setAppenders(appendersList);

        List users = ((UsersTableModel)usersTable.getModel()).getUsers();
        if (users == null) {
            users = new ArrayList();
        }
        config.setUsersWithLevelALL(users);
    }

    /**
     * Retrieves the inserted values and, if all inputs are valid, calls back
     * the controller to actually store the new logger configuration to the
     * server.
     *
     */
    private void setLoggerConfiguration() {
        Log.debug("setLoggerConfiguration");

        //
        // First of all, perform input validation
        //
        try {
            validateInput();
        } catch (IllegalArgumentException e) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(e.getMessage());
            DialogDisplayer.getDefault().notify(desc);

            return;
        }

        //
        // Read the inserted values
        //
        getValues();

        LoggerConfiguration config = loggerNode.getConfig();

        try {
            ExplorerUtil.getSyncAdminController()
                        .getServerSettingsController()
                        .setLoggerConfiguration(config);

        } catch (AdminException e) {
            String msg = Bundle.getMessage(Bundle.ERROR_SAVING_LOGGER_CONFIGURATION);
            NotifyDescriptor desc = new NotifyDescriptor.Message(e.getMessage());
            DialogDisplayer.getDefault().notify(desc);

            Log.error(msg, e);

            return;
        }

        Log.info(Bundle.getMessage(Bundle.MSG_LOGGER_CONFIGURATION_SAVED));
    }

}

/**
 * Inner class used for the appenders list
 */
class AppendersListModel extends AbstractListModel {

    Vector appendersList = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create new model
     */
    public AppendersListModel() {
        appendersList = new Vector();
    }

    /**
     * Create new appenders list Model.
     * @param roles Hashtable the roles to be loaded inn the list.
     */
    public AppendersListModel(java.util.List appendersList) {
        this();
        this.appendersList = new Vector(appendersList);
    }

    // ---------------------------------------------------------- public methods

    /**
     *
     * @param position int the element index to obtain
     * @return Object the contained item
     */
    public Object getElementAt(int position) {
        Object id = appendersList.elementAt(position);
        return id;
    }

    /**
     * the size of the list
     * @return int the list size
     */
    public int getSize() {
        return appendersList.size();
    }

    /**
     * Returns an array of the index corresponding to the given appenders
     * @param appenders the list of the appender names to find in the JList
     * @return int[] the array of the appender position
     */
    public int[] getIndices(List appenders) {
        if (appenders == null || appenders.size() == 0) {
            return new int[0];
        }
        int numAppendersInJList = this.appendersList.size();
        int numAppenders        = appenders.size();

        String appenderInJList = null;
        int[]  indices         = new int[numAppenders];
        int cont = 0;
        for (int i = 0; i < numAppendersInJList; i++) {
            appenderInJList = (String) (appendersList.elementAt(i));
            for (int j = 0; j < numAppenders; j++) {
                if (appenderInJList.equals(appenders.get(j))) {
                    indices[cont++] = i;
                }
            }
        }
        return indices;
    }

    public void setAvailableAppenders(java.util.List availableAppenders) {
        appendersList = new Vector(availableAppenders);
    }
}

class UsersTableModel  extends AbstractTableModel {

    // ------------------------------------------------------------ Private data
    /** data to be displayed in table */
    private java.util.List users = null;

    /** the names of the columns */
    private String[] columns = {};

    // ------------------------------------------------------------- Constructor
    /**
     *  Creates a new instance of UsersTableModel
     *
     *  @param map the hasmap with the informations to be displayed in the table
     *  @param columns array of string with the names of the columns
     */
    public UsersTableModel(java.util.List users, String[] columns) {
        this.users   = users;
        if (this.users == null) {
            this.users = new ArrayList();
        }
        this.columns = columns;
    }

    // ----------------------------------------------------------- Public Methods

    /**
     */
    public void setUsers(List users) {
        this.users = users;
    }

    /**
     *  Sets the names of the columns of the table
     *
     *  @param columns the array with the names of columns
     */
    public void setColumns(String[] columns) {
        this.columns=columns;
    }

    /**
     * Returns the array of string with the names of columns
     *
     *@return the array with the names of columns
     */
    public String[] getColumns() {
        return this.columns;
    }

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return users.size();
    }

    /**
     * Returns the name of the column at col
     *
     * @param col the index of the column
     *
     * @return the name of the column
     */
    public String getColumnName(int col) {
        return columns[col];
    }

    /**
     * Returns the number of columns in the model
     *
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns true if the cell at row and col is editable. Otherwise,
     * setValueAt on the cell will not change the value of that cell.
     *
     * @param row the row whose value to be queried
     * @param col the column whose value to be queried
     *
     * @return true if the cell is editable
     */
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * Returns the value for the cell at col and row.
     *
     * @param row the row whose value is to be queried
     * @param col the column whose value is to be queried
     *
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int row, int col) {

        if (col == 0) {
            return users.get(row);
        }
        return "";
    }

    /**
     * Sets the value in the cell at col and row to value.
     *
     * @param value the new value
     * @param row   the row whose value is to be changed
     * @param col   the column whose value is to be changed
     */
    public void setValueAt(Object value, int row, int col) {
        if(value.toString().equals("")) {
            return;
        }
        users.set(row, value);
    }

    /**
     * Removes an user from table.
     */
    public void removeItem(int row) {
        if(row < 0) {
            return;
        }
        //find the item in map and remove
        int answer = JOptionPane.showConfirmDialog(
                        null, Bundle.getMessage(
                                Bundle.DTM_MSG_DELETE),
                        Bundle.getMessage(
                                Bundle.DTM_MSG_DELETE_TITLE),
                        JOptionPane.YES_NO_OPTION);

        if (answer == 0) {
            users.remove(row);
            fireTableDataChanged();
        }

    }

    /**
     * Adds a new element to the users table.
     * Defalut values are read from properties file
     */
    public int addItem() {
        String newItem = "user name";

        String key = newItem;

        int i=1;
        while( users.contains(key) ) {
            key = newItem + "_" + i;
            i++;
        }
        users.add(key);
        fireTableDataChanged();
        return users.size()-1;
    }

    /**
     * Returns the users list
     * @return the users list
     */
    public List getUsers() {
        return users;
    }

    /**
     * Sorts the displayed users ignoring the case
     */
    public void sortUsers() {
        Collections.sort(users, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String)o1;
                String s2 = (String)o2;
                s1 = s1.toLowerCase();
                s2 = s2.toLowerCase();
                return s1.compareTo(s2);
            }

            public boolean equals(Object o1, Object o2) {
                String s1 = (String)o1;
                String s2 = (String)o2;
                s1 = s1.toLowerCase();
                s2 = s2.toLowerCase();
                return s1.equals(s2);
            }
        });
        fireTableDataChanged();
    }
}
