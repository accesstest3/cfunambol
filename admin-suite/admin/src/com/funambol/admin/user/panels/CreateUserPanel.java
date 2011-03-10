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

import com.funambol.admin.ui.PanelManager;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Generate a panel used to insert a user.
 * The panel includes all the necessary fields to create a user:
 * userName, password, role, name, email and so on.
 *
 * @version $Id: CreateUserPanel.java,v 1.11 2008-06-24 12:49:37 piter_may Exp $
 **/
public class CreateUserPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    /** constant for query mode */
    private static int MODE_UPDATE = 0;
    private static int MODE_INSERT = 1;

    // ------------------------------------------------------------ Private data
    /** panel mode (insert or update) */
    private int mode = MODE_INSERT;

    /** label for the panel's name */
    private JLabel panelName = new JLabel();

    /** label for the userName */
    private JLabel userName = new JLabel();

    /** TextField for the userName */
    private JTextField jTUser = new JTextField();

    /** label for the role */
    private JLabel role = new JLabel();

    /** list for the roles */
    private JList jLRole = null;

    /** label for the password */
    private JLabel password = new JLabel();

    /** TextField for the password */
    private JTextField jTPassword = new JPasswordField();

    /** label for confirming the password */
    private JLabel confPassword = new JLabel();

    /** TextField for confirming the password */
    private JTextField jTConfPassword = new JPasswordField();

    /** label for the firstName */
    private JLabel firstName = new JLabel();

    /** TextField for the firstName */
    private JTextField jTFirstName = new JTextField();

    /** label for the lastName */
    private JLabel lastName = new JLabel();

    /** TextField for the lastName */
    private JTextField jTLastName = new JTextField();

    /** label for the email */
    private JLabel email = new JLabel();

    /** TextField for the email */
    private JTextField jTEmail = new JTextField();

    /** Insert  button*/
    private JButton jBInsert = new JButton();
    
    /** Cancel  button*/
    private JButton jBCancel = new JButton();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** Internal list of available roles */
    private Hashtable availableRoles;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for user insert.
     */
    public CreateUserPanel(Hashtable roles) {
        this.availableRoles = roles;

        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }

    // ---------------------------------------------------------- Public methods

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 500);
    }
    
    /**
     * set the role of the user
     * @param role String the role to load
     */
    public void setRole(String role) {
        if (role.equals(Bundle.getMessage(Bundle.LABEL_NODE_ROLES))) {
            jLRole.clearSelection();
        } else {
            jLRole.setSelectedValue( (String)role, true);
        }
    }

    /**
     * set the roles and update the UI
     * @param roles Hashtable the source of the roles
     */
    public void setRoles(Hashtable roles) {
        ( (RolesListModel)jLRole.getModel()).loadRoles(roles);
        jLRole.updateUI();
    }

    /**
     * load the user into the panel
     * @param syncUser the user
     */
    public void loadUser(Sync4jUser syncUser) {

        mode = MODE_UPDATE;

        panelName.setText(Bundle.getMessage(Bundle.EDIT_USER_PANEL_NAME));
        this.setName(Bundle.getMessage(Bundle.EDIT_USER_PANEL_NAME));

        jBInsert.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));

        jTUser.setEditable(false);
        jTUser.setText(syncUser.getUsername());
        jTPassword.setText(syncUser.getPassword());
        jTConfPassword.setText(syncUser.getPassword());
        jTFirstName.setText(syncUser.getFirstname());
        jTLastName.setText(syncUser.getLastname());
        jTEmail.setText(syncUser.getEmail());
        String[] roles = syncUser.getRoles();

        selectRolesInJList(roles);
    }

    /**
     * prepare the panel for a new user insertion.
     */
    public void prepareForNewUser() {
        panelName.setText(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));
        this.setName(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));

        mode = MODE_INSERT;

        jTUser.setEditable(true);
    }

    // --------------------------------------------------------- Private methods

    /**
     * fills the combo
     * @param roles String[]
     */
    private void selectRolesInJList(String[] roles) {
        int[] indexToSelect = ( (RolesListModel)jLRole.getModel()).getIndex(roles);
        jLRole.setSelectedIndices(indexToSelect);
    }

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel.
     */
    private void init() throws Exception {
        this.setAutoscrolls(true);
        this.setLayout(null);
        this.setName(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));

        titledBorder1 = new TitledBorder("");
        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 6, 216, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        role.setText(Bundle.getMessage(Bundle.USER_PANEL_ROLES) + " :");
        role.setBounds(new Rectangle(15, 265, 111, 18));

        RolesListModel listModel = new RolesListModel(availableRoles);
        jLRole = new JList(listModel);

        jLRole.setFont(GuiFactory.defaultFont);
        JScrollPane scrollPane = new JScrollPane(jLRole);
        scrollPane.setBounds(new Rectangle(131, 266, 132, 73));
        scrollPane.setAutoscrolls(true);
        //scrollPane.setName(Bundle.getMessage(Bundle.USER_PANEL_ROLES));

        userName.setText(Bundle.getMessage(Bundle.USER_PANEL_USERNAME) + " :");
        userName.setBounds(new Rectangle(15, 49, 111, 18));
        jTUser.setBounds(new Rectangle(131, 49, 132, 18));

        password.setText(Bundle.getMessage(Bundle.USER_PANEL_PASSWORD) + " :");
        password.setBounds(new Rectangle(15, 88, 111, 18));
        jTPassword.setBounds(new Rectangle(131, 88, 132, 18));

        confPassword.setText(Bundle.getMessage(Bundle.USER_PANEL_CONFPASSWORD) + " :");
        confPassword.setBounds(new Rectangle(15, 125, 111, 18));
        jTConfPassword.setBounds(new Rectangle(131, 125, 132, 18));

        firstName.setText(Bundle.getMessage(Bundle.USER_PANEL_FIRSTNAME) + " :");
        firstName.setBounds(new Rectangle(15, 160, 111, 18));
        jTFirstName.setBounds(new Rectangle(131, 160, 132, 18));

        lastName.setBounds(new Rectangle(15, 194, 111, 18));
        lastName.setText(Bundle.getMessage(Bundle.USER_PANEL_LASTNAME) + " :");
        jTLastName.setBounds(new Rectangle(131, 194, 132, 18));

        email.setText(Bundle.getMessage(Bundle.USER_PANEL_EMAIL) + " :");
        email.setBounds(new Rectangle(15, 228, 111, 18));
        jTEmail.setBounds(new Rectangle(131, 228, 132, 18));

        jBInsert.setBounds(new Rectangle(80, 360, 65, 28));
        jBInsert.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD));

        jBInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUser();
            }
        });

        jBCancel.setBounds(new Rectangle(150, 360, 70, 28));
        jBCancel.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
 
        jBCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });
        
        jBInsert.setFont(GuiFactory.defaultFont);
        jBCancel.setFont(GuiFactory.defaultFont);
        jTFirstName.setFont(GuiFactory.defaultFont);
        email.setFont(GuiFactory.defaultFont);
        password.setFont(GuiFactory.defaultFont);
        jTConfPassword.setFont(GuiFactory.defaultFont);
        jTLastName.setFont(GuiFactory.defaultFont);
        jTPassword.setFont(GuiFactory.defaultFont);
        jTUser.setFont(GuiFactory.defaultFont);
        lastName.setFont(GuiFactory.defaultFont);
        confPassword.setFont(GuiFactory.defaultFont);
        jTEmail.setFont(GuiFactory.defaultFont);
        userName.setFont(GuiFactory.defaultFont);
        firstName.setFont(GuiFactory.defaultFont);
        role.setFont(GuiFactory.defaultFont);

        this.add(panelName);
        this.add(userName);
        this.add(jTUser);
        this.add(password);
        this.add(jTPassword);
        this.add(confPassword);
        this.add(jTConfPassword);
        this.add(firstName);
        this.add(jTFirstName);
        this.add(lastName);
        this.add(jTLastName);
        this.add(email);
        this.add(jTEmail);
        this.add(role);
        this.add(scrollPane);
        this.add(jBInsert);
        this.add(jBCancel);

    }

    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    } 
    
    /**
     * Set the current user and call insert or update on it.
     */
    private void setUser() {
        String password = jTPassword.getText();
        String confpassword = jTConfPassword.getText();

        // check password
        if (!password.equals(confpassword)) {
            // password and checkpassword are different
            // prepare and dislay error message
            String error = Bundle.getMessage(Bundle.USER_PANEL_PASSWORD) + " and " +
                           Bundle.getMessage(Bundle.USER_PANEL_CONFPASSWORD) + " " +
                           Bundle.getMessage(Bundle.USER_MESSAGE_CHECK_PASSWORD);
            NotifyDescriptor desc = new NotifyDescriptor.Message(error);
            DialogDisplayer.getDefault().notify(desc);
            // clean password and checkpassword fields
            jTPassword.setText("");
            jTConfPassword.setText("");

            return;
        }

        Sync4jUser user = new Sync4jUser();        
        user.setUsername(jTUser.getText());
        user.setFirstname(jTFirstName.getText());
        user.setLastname(jTLastName.getText());
        user.setEmail(jTEmail.getText());
        user.setPassword(jTPassword.getText());

        int[] indexSelected = jLRole.getSelectedIndices();

        String[] rolesSelectoed = ( (RolesListModel)jLRole.getModel()).getRolesId(indexSelected);

        user.setRoles(rolesSelectoed);

        if (mode == MODE_INSERT) {
            boolean userSaved = false;

            try {
                userSaved = ExplorerUtil.getSyncAdminController().getUsersController().
                            insertNewUser(user);
            } catch (AdminException e) {
                Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
            }

            if (userSaved) {
                cleanForm();
            }
        } else {
            boolean userUpdated = false;

            try {
                userUpdated = ExplorerUtil.getSyncAdminController().getUsersController().updateUser(
                    user);
            } catch (AdminException e) {
                Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
            }
        }

    }

    /**
     * clean the user form.
     */
    private void cleanForm() {
        // clean textfiels
        jTUser.setText("");
        jTPassword.setText("");
        jTConfPassword.setText("");
        jTFirstName.setText("");
        jTLastName.setText("");
        jTEmail.setText("");
        jLRole.clearSelection();
    }

}

/**
 * Inner class used for the encapsulation of the roles.
 */
class RolesListModel extends AbstractListModel {

    /** roles inner table */
    Hashtable roles = null;
    /** List with roles id */
    Vector listRolesId = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create new role list Model.
     */
    public RolesListModel() {
        listRolesId = new Vector();
    }

    /**
     * Create new role list Model filled with the roles Table.
     * @param roles Hashtable the roles to be loaded inn the list.
     */
    public RolesListModel(Hashtable roles) {
        this();
        loadRoles(roles);
    }

    // ---------------------------------------------------------- public methods
    /**
     * load the roles in the list
     * @param roles Hashtable the table with the roles to load
     */
    public void loadRoles(Hashtable roles) {
        this.roles = roles;
        listRolesId.removeAllElements();
        listRolesId.addAll(roles.keySet());
    }

    /**
     *
     * @param position int the element index to obtain
     * @return Object the contained item
     */
    public Object getElementAt(int position) {
        Object id = listRolesId.elementAt(position);
        return roles.get(id);
    }

    /**
     * the size of the list
     * @return int the list size
     */
    public int getSize() {
        return listRolesId.size();
    }

    /**
     * Returns the array of roles ID
     * @param indexSelected int[] the array with the selected index
     * @return String[] the roles id returned
     */
    public String[] getRolesId(int[] indexSelected) {
        int numRoles = indexSelected.length;
        String[] rolesId = new String[numRoles];
        String roleId = null;
        for (int i = 0; i < numRoles; i++) {
            rolesId[i] = (String) (this.listRolesId.elementAt(indexSelected[i]));
        }
        return rolesId;
    }

    /**
     * Returns an array of the index corresponding to the String roles passed in
     * @param rolesId String[] the rolesid array to find in the list
     * @return int[] the array of the roles position
     */
    public int[] getIndex(String[] rolesId) {
        int numRolesInList = listRolesId.size();
        int numRoles = rolesId.length;
        String roleInList = null;
        int[] indexSelected = new int[numRoles];
        int cont = 0;
        for (int i = 0; i < numRolesInList; i++) {
            roleInList = (String) (listRolesId.elementAt(i));
            for (int j = 0; j < numRoles; j++) {
                if (roleInList.equals(rolesId[j])) {
                    indexSelected[cont++] = i;
                }
            }
        }
        return indexSelected;
    }

}
