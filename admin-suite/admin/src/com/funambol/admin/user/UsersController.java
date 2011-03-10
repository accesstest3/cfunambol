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
package com.funambol.admin.user;

import java.text.MessageFormat;

import java.util.Hashtable;

import com.funambol.framework.filter.*;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.AdminException;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.user.panels.CreateUserPanel;
import com.funambol.admin.user.panels.SearchUserPanel;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Manages all process about the users.
 *
 * @version $Id: UsersController.java,v 1.9 2007-11-28 10:28:19 nichele Exp $
 */
public class UsersController
implements Constants{

    /** constant for search query identifing username */
    private static String PARAM_USER_NAME = "username";

    // ------------------------------------------------------------ Private data
    private BusinessDelegate businessDelegate = null;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new <code>UsersController</code>.
     */
    public UsersController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Starts new user creation process.
     * @param role the role of the user
     * @throws AdminException if a error occurs during start the process
     */
    public void startNewUserProcess(String role) throws AdminException {

        Log.debug("Start new user process");

        CreateUserPanel panel = (CreateUserPanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME)
         );

        if (panel != null) {
            panel.prepareForNewUser();
            PanelManager.showPanel(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));
            panel.setRole(role);
        } else {
            // create new panel
            Hashtable availableRoles = null;

            boolean useCache = true;
            availableRoles = businessDelegate.getRoles(useCache);

            panel = new CreateUserPanel(availableRoles);
            panel.setRole(role);
            panel.validate();
            PanelManager.addPanel(panel);
        }

    }

    /**
     * Starts the process of edit a user
     * @param userToEdit the user to edit
     * @throws AdminException if a error occurs during start the edit process
     */
    public void startEditUserProcess(Sync4jUser userToEdit) throws AdminException {

        Log.debug("Start edit user process");

        CreateUserPanel panel = (CreateUserPanel)PanelManager.getPanel(Bundle.getMessage(Bundle.
            EDIT_USER_PANEL_NAME));

        if (panel != null) {
            panel.loadUser(userToEdit);
            PanelManager.showPanel(Bundle.getMessage(Bundle.EDIT_USER_PANEL_NAME));
        } else {
            // create new panel
            Hashtable availableRoles = null;

            boolean useCache = true;
            availableRoles = businessDelegate.getRoles(useCache);
            panel = new CreateUserPanel(availableRoles);
            panel.loadUser(userToEdit);
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Starts the search of the users
     */
    public void startSearchUserProcess() {
        SearchUserPanel panel = (SearchUserPanel)PanelManager.getPanel(Bundle.getMessage(Bundle.
            SEARCH_USER_PANEL_NAME));

        if (panel != null) {
            PanelManager.showPanel(Bundle.getMessage(Bundle.SEARCH_USER_PANEL_NAME));
        } else {
            // create new panel
            panel = new SearchUserPanel();
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Insert new user.
     * @param user the user to be added, via the <code>BusinessDelegate</code>
     * @throws AdminException if a error occurs during the insert
     * @return boolean true if the user is saved, false otherwise.
     */
    public boolean insertNewUser(Sync4jUser user) throws AdminException {

        boolean userSaved = false;

        boolean userOk = checkUserValue(user);

        if (!userOk) {
            return userSaved;
        }

        String username = user.getUsername();

        // check if username already exists
        Clause clause = getClauseForUserSearchById(username);

        Sync4jUser[] users = null;

        users = businessDelegate.getUsers(clause);

        if (users.length > 0) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.USER_MESSAGE_USERNAME_EXISTS),
                    new String[] {username}
                )
            );
            DialogDisplayer.getDefault().notify(desc);

            return userSaved;
        }

        businessDelegate.addUser(user);
        Log.info(Bundle.getMessage(Bundle.USER_MESSAGE_INSERT_OK));
        userSaved = true;

        return userSaved;
    }

    /**
     * Update a user, via the <code>Businessdelegate</code>.
     * @param user the user to update
     * @throws AdminException if a error occurs during the update
     * @return boolean true if the user is updated, false otherwise.
     */
    public boolean updateUser(Sync4jUser user) throws AdminException {

        boolean userUpdated = false;

        boolean userOk = checkUserValue(user);

        if (!userOk) {
            return userUpdated;
        }

        businessDelegate.setUser(user);
        Log.info(Bundle.getMessage(Bundle.USER_MESSAGE_EDIT_OK));
        userUpdated = true;

        return userUpdated;

    }

    /**
     * Search the users by Clause, and load them in the SearchUserPanel
     * @param clause The clause for specifying the search parameters
     * @throws AdminException if a error occurs during the search
     */
    public void searchUsers(Clause clause) throws AdminException {
        int numOfUsers = businessDelegate.countUsers(clause);
        String message = null;
        switch (numOfUsers) {
            case 0:
                message = Bundle.getMessage(Bundle.USER_MESSAGE_USER_NOT_FOUND);
                break;
            case 1:
                message = Bundle.getMessage(Bundle.USER_MESSAGE_USER_FOUND);
                break;
            default:
                message = MessageFormat.format(Bundle.getMessage(Bundle.
                    USER_MESSAGE_USERS_FOUND),
                    new String[] {String.valueOf(numOfUsers)});
        }

        Log.info(message);

        int maxNumOfResults = AdminConfig.getAdminConfig().getMaxResults();

        if (numOfUsers > maxNumOfResults) {
            Object[] arguments = {new Integer(numOfUsers)};

            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(Bundle.getMessage(Bundle.MESSAGE_MAX_NUMBER_OF_RESULTS),
                                     arguments));
            DialogDisplayer.getDefault().notify(desc);

            return;
        }

        Sync4jUser[] users = businessDelegate.getUsers(clause);

        SearchUserPanel searchUserPanel = (SearchUserPanel)PanelManager.getPanel(Bundle.
            getMessage(
            Bundle.SEARCH_USER_PANEL_NAME));

        if (searchUserPanel != null) {
            searchUserPanel.loadUsers(users);
        }
    }

    /**
     * Delete the user, via the BusinessDelegate class.
     * @param userToDelete Sync4jUser the user to be deleted
     * @throws AdminException if a error occurs during the delete
     * @return boolean true if the user is deleted, false otherwise.
     */
    public boolean deleteUser(Sync4jUser userToDelete) throws AdminException {

        boolean userDeleted = false;

        String currentUser = AdminConfig.getAdminConfig().getUser();
        if(userToDelete.getUsername().equals(currentUser)) {
            NotifyDescriptor.Message mess = new NotifyDescriptor.Message(
                Bundle.getMessage(Bundle.USER_DELETE_NOT_ALLOWED));
                Object nnd = DialogDisplayer.getDefault().notify(mess);
        } else {
            if (showConfirmDelete()) {
                businessDelegate.deleteUser(userToDelete.getUsername());
                Log.info(Bundle.getMessage(Bundle.USER_MESSAGE_DELETE_OK));
                userDeleted = true;
            }
        }
        return userDeleted;
    }

    /**
     * Refresh the roles.
     */
    public void refresh() {
        Log.debug("Refreshing roles...");
        Hashtable roles = new Hashtable();

        try {
            roles = businessDelegate.getRoles(false);
        } catch (Exception e) {
            Log.error(e.getMessage());
        }

        Log.debug("Roles: " + roles);

        CreateUserPanel userPanel =
            (CreateUserPanel)PanelManager.getPanel(Bundle.getMessage(Bundle.INSERT_USER_PANEL_NAME));

        if (userPanel != null) {
            userPanel.setRoles(roles);
        }

        userPanel = (CreateUserPanel)PanelManager.getPanel(Bundle.getMessage(Bundle.EDIT_USER_PANEL_NAME));
        if (userPanel != null) {
            userPanel.setRoles(roles);
        }

        SearchUserPanel searchPanel = (SearchUserPanel)PanelManager.getPanel(Bundle.getMessage(
            Bundle.
            SEARCH_USER_PANEL_NAME));

        if (searchPanel != null) {
            searchPanel.setRoles(roles);
        }

    }

    // --------------------------------------------------------- Private Methods

    /**
     * Check the correctness of the data in the user object
     * @param user Sync4jUser the object to validate
     * @return boolean true if the values are correct, false otherwise.
     */
    private boolean checkUserValue(Sync4jUser user) {

        String username = user.getUsername();
        String password = user.getPassword();
        String firstName = user.getFirstname();
        String lastName = user.getLastname();
        String email = user.getEmail();

        String[] roles = user.getRoles();

        // controls edited fields
        StringBuffer check = new StringBuffer();
        if (username.equalsIgnoreCase("") || username.equalsIgnoreCase(null)) {
            check.append(Bundle.getMessage(Bundle.USER_PANEL_USERNAME)).append(", ");
        }
        if (password.equalsIgnoreCase("") || password.equalsIgnoreCase(null)) {
            check.append(Bundle.getMessage(Bundle.USER_PANEL_PASSWORD)).append(", ");
        }
        if (firstName.equalsIgnoreCase("") || firstName.equalsIgnoreCase(null)) {
            check.append(Bundle.getMessage(Bundle.USER_PANEL_FIRSTNAME)).append(", ");
        }
        if (lastName.equalsIgnoreCase("") || lastName.equalsIgnoreCase(null)) {
            check.append(Bundle.getMessage(Bundle.USER_PANEL_LASTNAME)).append(", ");
        }
        if (email.equalsIgnoreCase("") || email.equalsIgnoreCase(null)) {
            check.append(Bundle.getMessage(Bundle.USER_PANEL_EMAIL)).append(", ");
        } else {
            //
            // The address should start with a sequence of alphanumerical,
            // underscores, dots or dashes.
            // Then comes an @.
            // After that there should be another sequence of alphanumerical
            // characters and dashes, followed by a dot.
            // And then another sequence of alphanumerical characters, but
            // without the dash.
            //
            String filter = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+[.])+([a-zA-Z0-9])+$";
            if (!email.matches(filter)) {
                check.append(Bundle.getMessage(Bundle.USER_PANEL_EMAIL)).append(", ");
            }
        }

        if (check.length() != 0) {

            check = new StringBuffer(Bundle.getMessage(Bundle.INPUT_DATA_ERROR_MESSAGE) + " " +
                                     check.substring(0, check.length() - 2) + ".");

            NotifyDescriptor desc = new NotifyDescriptor.Message(check);
            DialogDisplayer.getDefault().notify(desc);
            return false;
        }

        // check selected role
        if (roles != null && roles.length == 0) {
            String error = Bundle.getMessage(Bundle.USER_MESSAGE_CHECK_ROLES);
            NotifyDescriptor desc = new NotifyDescriptor.Message(error);
            DialogDisplayer.getDefault().notify(desc);

            return false;
        }

        return true;
    }

    /**
     * Create and return the clause for the user search with the given username.
     * @param username the username
     * @return Clause the clause for the user search with the given username.
     */
    private Clause getClauseForUserSearchById(String username) {
        Clause clause = null;

        String[] param = {PARAM_USER_NAME};
        String[] value = {username};
        String[] operator = {WhereClause.OPT_EQ};

        clause = ClauseTools.createClause(param, operator, value);
        return clause;

    }

    /**
     * Display a notification message to ask user if he wants to delete value of
     * the selected user.
     */
    private boolean showConfirmDelete() {
        boolean ret = false;
        // prepare notification message
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(
            Bundle.getMessage(Bundle.USER_QUESTION_DELETE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_DELETE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        // ok , so update values
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {
            ret = true;
        }
        return ret;
    }

}
