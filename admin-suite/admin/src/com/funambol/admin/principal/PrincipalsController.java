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
package com.funambol.admin.principal;

import java.text.MessageFormat;

import java.util.ArrayList;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.LastTimestamp;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.AdminException;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.principal.panels.CreatePrincipalPanel;
import com.funambol.admin.principal.panels.LastTimestampsPanel;
import com.funambol.admin.principal.panels.SearchPrincipalPanel;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Manages all process about the principals.
 *
 * @version $Id: PrincipalsController.java,v 1.9 2007-11-28 10:28:19 nichele Exp $
 */
public class PrincipalsController
implements Constants{

    // --------------------------------------------------------------- Constants

    /** constant for search query identifing principal */
    private static String PARAM_PRINCIPAL = "principal";

    /** constant to define search operator */
    private static final String LABEL_OPERATOR_EXACTLY = "Exactly";
    
    // ------------------------------------------------------------ Private data
    private BusinessDelegate businessDelegate = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates new PrincipalsController with the given <code>BusinessDelegate</code>
     *
     * @param businessDelegate the BusinessDelegate to call business method
     */
    public PrincipalsController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Starts the process to create new principal.
     */
    public void startNewPrincipalProcess() {

        Log.debug("Start new principal process");

        CreatePrincipalPanel createPanel = (CreatePrincipalPanel)PanelManager.getPanel(Bundle.
            getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));
        if (createPanel != null) {
            PanelManager.showPanel(Bundle.getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));
        } else {

            createPanel = new CreatePrincipalPanel();
            createPanel.validate();

            PanelManager.addPanel(createPanel);
        }
    }

    /**
     * Starts process to search the principals
     */
    public void startSearchPrincipalProcess() {
        SearchPrincipalPanel panel = (SearchPrincipalPanel)PanelManager.getPanel(Bundle.getMessage(
            Bundle.SEARCH_PRINCIPAL_PANEL_NAME));

        if (panel != null) {
            PanelManager.showPanel(Bundle.getMessage(Bundle.SEARCH_PRINCIPAL_PANEL_NAME));
        } else {
            // create new panel
            panel = new SearchPrincipalPanel();
            panel.validate();
            PanelManager.addPanel(panel);
        }

    }

    /**
     * Delete principal.
     * @param principalToDelete principal to delete
     * @throws AdminException if a error occurs during the delete
     * @return boolean true if the principal is deleted, false otherwise.
     */
    public boolean deletePrincipal(Sync4jPrincipal principalToDelete) throws AdminException {

        boolean principalDeleted = false;
        if (showConfirmDelete()) {
            businessDelegate.deletePrincipal(principalToDelete.getId());
            Log.info(Bundle.getMessage(Bundle.PRINCIPAL_MESSAGE_DELETE_OK));
            principalDeleted = true;
        }

        return principalDeleted;

    }

    /**
     * Searchs principals in according with the given clause.
     * @param clause clause for principals search
     * @throws AdminException if a error occurs during the search
     */
    public void searchPrincipals(Clause clause) throws AdminException {
        int numOfPrincipals = businessDelegate.countPrincipals(clause);
        String message = null;
        switch (numOfPrincipals) {
            case 0:
                message = Bundle.getMessage(Bundle.PRINCIPAL_MESSAGE_PRINCIPAL_NOT_FOUND);
                break;
            case 1:
                message = Bundle.getMessage(Bundle.PRINCIPAL_MESSAGE_PRINCIPAL_FOUND);
                break;
            default:
                message = MessageFormat.format(Bundle.getMessage(Bundle.
                    PRINCIPAL_MESSAGE_PRINCIPALS_FOUND),
                    new String[] {String.valueOf(numOfPrincipals)});
        }

        Log.info(message);

        int maxNumOfResults = AdminConfig.getAdminConfig().getMaxResults();

        if (numOfPrincipals > maxNumOfResults) {
            Object[] arguments = {new Integer(numOfPrincipals)};

            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(Bundle.getMessage(Bundle.MESSAGE_MAX_NUMBER_OF_RESULTS),
                                     arguments));
            DialogDisplayer.getDefault().notify(desc);

            return;
        }

        Sync4jPrincipal[] principals = businessDelegate.getPrincipals(clause);

        SearchPrincipalPanel searchPrincipalPanel = (SearchPrincipalPanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.SEARCH_PRINCIPAL_PANEL_NAME));

        if (searchPrincipalPanel != null) {
            searchPrincipalPanel.loadPrincipals(principals);
        }
    }

    /**
     * Searchs users for principal creation.
     * @param clause clause for users search
     * @throws AdminException if a error occcurs during the search
     */
    public void searchUsersForPrincipal(Clause clause) throws AdminException {
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

        CreatePrincipalPanel createPrincipalPanel = (CreatePrincipalPanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));

        if (createPrincipalPanel != null) {
            createPrincipalPanel.loadUsers(users);
        }
    }

    /**
     * Searchs devices for principal creation.
     * @param clause clause for devices search
     * @throws AdminException if a error occurs during the search
     */
    public void searchDevicesForPrincipal(Clause clause) throws AdminException {
        int maxNumOfResults = AdminConfig.getAdminConfig().getMaxResults();

        int numOfDevices = businessDelegate.countDevices(clause);

        String message = null;
        switch (numOfDevices) {
            case 0:
                message = Bundle.getMessage(Bundle.DEVICE_MESSAGE_DEVICE_NOT_FOUND);
                break;
            case 1:
                message = Bundle.getMessage(Bundle.DEVICE_MESSAGE_DEVICE_FOUND);
                break;
            default:
                message = MessageFormat.format(Bundle.getMessage(Bundle.
                    DEVICE_MESSAGE_DEVICES_FOUND),
                    new String[] {String.valueOf(numOfDevices)});
        }

        Log.info(message);

        if (numOfDevices > maxNumOfResults) {
            Object[] arguments = {new Integer(numOfDevices)};

            NotifyDescriptor desc = new NotifyDescriptor.Message(
                MessageFormat.format(Bundle.getMessage(Bundle.MESSAGE_MAX_NUMBER_OF_RESULTS),
                                     arguments));
            DialogDisplayer.getDefault().notify(desc);

            return;
        }

        // search devices with clause
        Sync4jDevice[] devices = businessDelegate.getDevices(clause);

        CreatePrincipalPanel createPrincipalPanel = (CreatePrincipalPanel)PanelManager.getPanel(
            Bundle.getMessage(Bundle.INSERT_PRINCIPAL_PANEL_NAME));

        if (createPrincipalPanel != null) {
            createPrincipalPanel.loadDevices(devices);
        }
    }

    /**
     * Insert the given principal.
     * @param principal principal to insert
     * @throws AdminException if a error occurs during the insert
     */
    public void insertNewPrincipal(Sync4jPrincipal principal) throws AdminException {
        long id = businessDelegate.addPrincipal(principal);
        // ok, principal inserted in server, notify it
        Log.info(Bundle.getMessage(Bundle.PRINCIPAL_MESSAGE_INSERT_OK));
    }

    /**
     * Starts process to show the principal last synchronization timestamps
     */
    public void startLastTimestampsProcess(Sync4jPrincipal principal) 
    throws AdminException {

        LastTimestamp[] lastTimestamps = searchLastTimestamps(getClause(principal));
        
        LastTimestampsPanel panel = (LastTimestampsPanel)PanelManager.getPanel(Bundle.getMessage(
            Bundle.LAST_TIMESTAMPS_PANEL_NAME));

        if (panel == null) {
            // create new panel
            panel = new LastTimestampsPanel();
            panel.validate();            
            panel.loadLastTimestamps(lastTimestamps);            
            panel.setPrincipal(principal);
            PanelManager.addPanel(panel);            
        } else {
            panel.loadLastTimestamps(lastTimestamps);            
            panel.setPrincipal(principal);
            PanelManager.showPanel(Bundle.getMessage(Bundle.LAST_TIMESTAMPS_PANEL_NAME));
        }        
    }

    /**
     * Delete last timstamp.
     * @param lastTimestamp last timstamp to delete
     * @throws AdminException if a error occurs during the delete
     * @return boolean true if the principal is deleted, false otherwise.
     */
    public boolean deleteLastTimestamp(LastTimestamp lastTimestamp) throws AdminException {

        boolean lastTimestampDeleted = false;
        if (showConfirmation(Bundle.getMessage(Bundle.LABEL_BUTTON_RESET),
                             Bundle.getMessage(Bundle.LAST_TIMESTAMP_QUESTION_RESET))) {
            businessDelegate.deleteLastTimestamp(lastTimestamp.principalId, lastTimestamp.database);
            Log.info(Bundle.getMessage(Bundle.LAST_TIMESTAMP_MESSAGE_RESET_OK));
            lastTimestampDeleted = true;
        }

        return lastTimestampDeleted;

    }

    // --------------------------------------------------------- Private methods

    /**
     * Display a notification message to ask user if he wants to delete value of
     * the selected principal.
     */
    private boolean showConfirmDelete() {
        boolean ret = false;
        // prepare notification message
        Confirmation nd = new NotifyDescriptor.Confirmation(
            Bundle.getMessage(Bundle.PRINCIPAL_QUESTION_DELETE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_DELETE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        // ok , so update values
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {
            ret = true;
        }
        return ret;
    }

    /**
     * Display a notification message to ask user if he wants to proceed.
     * @param title the message box title
     * @param msg message to display
     */
    private boolean showConfirmation(String title, String msg) {
        boolean ret = false;
        // prepare notification message
        Confirmation nd = new NotifyDescriptor.Confirmation(
            msg,
            title,
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        // ok , so update values
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {
            ret = true;
        }
        return ret;
    }

    /**
     * Searchs timestamp in according with the given clause.
     * @param clause clause for last timestamp search
     * @throws AdminException if a error occurs during the search
     */
    private LastTimestamp[] searchLastTimestamps(Clause clause) throws AdminException {
        int numOfLastTimestamps = businessDelegate.countLastTimestamps(clause);
        String message = null;
        switch (numOfLastTimestamps) {
            case 0:
                message = Bundle.getMessage(Bundle.LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_NOT_FOUND);
                break;
            case 1:
                message = Bundle.getMessage(Bundle.LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMP_FOUND);
                break;
            default:
                message = MessageFormat.format(Bundle.getMessage(Bundle.
                    LAST_TIMESTAMP_MESSAGE_LAST_TIMESTAMPS_FOUND),
                    new String[] {String.valueOf(numOfLastTimestamps)});
        }

        Log.info(message);

        LastTimestamp[] lastTimestamps = businessDelegate.getLastTimestamps(clause);
        return lastTimestamps;
    }
    
    /**
     * Create a clause with selected principal
     *
     * @param principal the selected principal
     *
     * @return Clause the object Clause
     */
    private Clause getClause(Sync4jPrincipal principal) {

        Clause clause = null;

        String[] param    = null;
        String[] value    = null;
        String[] operator = null;

        ArrayList paramList    = new ArrayList();
        ArrayList valueList    = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;
        if (principal.getId() != -1) {

            paramList.add(PARAM_PRINCIPAL);
            valueList.add(String.valueOf(principal.getId()));
            operatorList.add(ClauseTools.operatorViewToOperatorValue(LABEL_OPERATOR_EXACTLY));
            i++;

        }

        param    = (String[])paramList.toArray(new String[0]);
        value    = (String[])valueList.toArray(new String[0]);
        operator = (String[])operatorList.toArray(new String[0]);

        clause   = ClauseTools.createClause(param, operator, value);

        return clause;
    }

}
