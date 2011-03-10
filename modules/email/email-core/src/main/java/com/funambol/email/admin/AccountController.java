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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.util.Log;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.admin.cache.CacheDetailsPanel;
import com.funambol.email.admin.user.SearchUserPanel;
import com.funambol.email.admin.dao.WSDAO;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * This class contains the logic and visual representation for mail server
 * account handling processes (insertion, modification, deletation, inspecting
 * cache).
 *
 * @version $Id: AccountController.java,v 1.1 2008-03-25 11:28:17 gbmiglia Exp $
 */
public class AccountController {

    // ------------------------------------------------------------ Private data

    /** data access object */
    private WSDAO WSDao;

    //
    // Domain objects
    //

    /** account */
    private MailServerAccount account;

    /** ds server user bound to the account */
    private Sync4jUser user;

    //
    // Visual elements
    //

    private SearchUserPanel searchUserPanel;
    private AccountDetailsPanel accountDetailsPanel;

    /**
     * How this process is represented, as a popup frame or in a admintool
     * panel.
     */
    private boolean isPopup;

    // Following components are meaningful only if isPopup is true.

    /** popup frame */
    private JDialog dialog;

    /** popup title */
    private String title;

    // -------------------------------------------------------------- Properties

    /**
     * Panel that contains all the steps of a process.
     */
    private Container mainPanel;

    /**
     * Gets the mainPanel property.
     * @return mainPanel
     */
    public Container getPanel() {
        return mainPanel;
    }

    /** parent frame */
    private Frame parentFrame;

    /** Sets the parent frame property */
    public void setParentFrame(Frame parent){
        this.parentFrame = parent;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of an AccountController object.
     *
     * @param isPopup flag that specify if processes will be displaied in a
     * popup window or a frame in the admin tool.
     */
    public AccountController(boolean isPopup){

        this.isPopup = isPopup;
        initWSDAO();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Select an user from a list of ds-server users.
     */
    public void selectUser(){

        searchUserPanel = new SearchUserPanel(new Step(){
            //
            // navigation logic:
            // go to "insert account step" when a row from the users table is
            // selected
            //
            public void goToNextStep() {

                //
                // insert account details
                //
                insertAccount();
            }
        });

        searchUserPanel.setWSDao(this.WSDao);

        mainPanel = searchUserPanel;

        if (isPopup){
            title = "Adding user account: select user";
            showAsPopup();
        }
    }

    /**
     * The account insertion process is performed through the following steps:
     * <p/>
     * 1 - select an user from a list of users (selectUser())
     * 2 - insert account specific informations (accountDetails())
     */
    public void insertAccount(){

        user = searchUserPanel.getUser();

        accountDetails(user, null, OperationType.ACCOUNT_INSERTION);

        if (isPopup){
            title = "Adding user account: insert account data";
            showAsPopup();
        }
    }

    /**
     * Account modification process.
     * @param account account to be modified
     * @return updated account
     */
    public MailServerAccount modifyAccount(MailServerAccount account){

        this.account = account;

        Sync4jUser user = new Sync4jUser();
        user.setUsername(this.account.getUsername());

        accountDetails(user, this.account, OperationType.ACCOUNT_UPDATE);

        if (isPopup){
            title = "Update account data";
            showAsPopup();
        }

        return accountDetailsPanel.getAccount();
    }

    /**
     * Account deletetion process.
     * <br/>
     * TODO deletetion of more than one account (by now only one account can be
     * deleted)
     *
     * @param account account to be deleted
     * @return number of records deleted
     */
    public int deleteAccount(MailServerAccount account){

        String[] options = {"Yes", "No"};
        String message = "Are you sure you want to delete account for user " +
                account.getUsername() + " ?";
        int choice = showMessagePopup(message, options);

        if (choice == JOptionPane.YES_OPTION){

            try {
                WSDao.deleteAccount(account.getId());
                Log.info("Account for user " + account.getUsername() + " deleted");
            } catch (InboxListenerException e) {  
                Log.info("Error while deleting account for user " + 
                        account.getUsername() + ": " + e.getMessage());                
                return 0;
            }
            return 1;
        }
        return 0;
    }

    /**
     * Shows the mail server data, for insertion or modification.
     * @param operationType operation that uses this panel instance
     * @param inAccount a mail server account
     * @param user ds server user bound to the account
     */
    private void accountDetails(
            Sync4jUser user,
            MailServerAccount inAccount,
            final OperationType operationType){

        accountDetailsPanel =
                new AccountDetailsPanel(user, inAccount, WSDao, operationType);

        mainPanel = accountDetailsPanel;

        accountDetailsPanel.setSaveAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //
                // Validates data (fills account object only if data are valid).
                //
                ValidationError error = accountDetailsPanel.validateData();

                //
                // if data are valid then get the account
                //
                if (error.getErrorStrings().size() == 0){

                    //
                    // If the account object is null then do insertion,  update
                    // otherwise.
                    //

                    accountDetailsPanel.fillAccount();
                    account = accountDetailsPanel.getAccount();
                    try {
                        if (operationType == OperationType.ACCOUNT_INSERTION){
                            WSDao.insertAccount(account);
                            Log.info("Account data saved");
                            closePopup();
                        } else
                        if (operationType == OperationType.ACCOUNT_UPDATE){
                            WSDao.updateAccount(account);
                            Log.info("Account data updated");
                            closePopup();
                        }

                    } catch (InboxListenerException e) {
                        Log.error(e);
                    }
                }
            }
        });

        accountDetailsPanel.setCancelAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closePopup();
            }
        });

    }

    /**
     * Shows cache entries for given username,protocol.
     */
    public void showCache(String username, String protocol){

        CacheDetailsPanel cacheDetailsPanel =
                new CacheDetailsPanel(username, protocol);
        cacheDetailsPanel.setWSDao(WSDao);

        cacheDetailsPanel.setCloseAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closePopup();
            }
        });

        cacheDetailsPanel.loadCacheEntries();

        mainPanel = cacheDetailsPanel;

        if (isPopup){
            title = "Cache details";
            showAsPopup();
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates this panel as a popup window according to the value of the
     * isPopup field.
     */
    private void initGui(){

        if (isPopup){

            //
            // creates popup window
            //
            if (dialog == null){
                dialog = new JDialog(parentFrame);
            }

            //
            // gets the container panel and shows the popup
            //
            //mainPanel = mainFrame.getContentPane();

            dialog.setModal(true);
            dialog.setResizable(false);

            dialog.setSize(870, 460);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension window = dialog.getSize();
            dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

        } else {
            //
            // TODO at present only popup mode is supported
            //
            mainPanel = new JPanel();
        }
    }

    /**
     * Initializes data access object.
     */
    private void initWSDAO() {

        String endPoint = "";
        String user = "";
        String pwd = "";
        int reloadingTime = 0;

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("http://")
                .append(AdminConfig.getAdminConfig().getHostName())
                .append(':')
                .append(AdminConfig.getAdminConfig().getServerPort())
                .append(AdminConfig.getAdminConfig().getContextPath())
                .append("/services/email");
            endPoint = sb.toString();

            user = AdminConfig.getAdminConfig().getUser();
            pwd = AdminConfig.getAdminConfig().getPassword();
            reloadingTime = 1;
                        
            WSDao = new WSDAO(new ServerInformation(endPoint, user, pwd), null);

        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Closes popup windows.
     */
    private void closePopup(){
        dialog.setVisible(false);
        dialog.dispose();
    }

    /**
     * Shows a given message in a popup window.
     *
     * @param message message to be displaied
     * @return selected option
     */
    private int showMessagePopup(String message){
        Object[] options = {"OK"};
        return JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
    }
    /**
     * Shows a given message with the given option in a popup window.
     *
     * @param message
     * @param options
     * @return selected option
     */
    private int showMessagePopup(String message, Object[] options){
        return JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
    }

    /**
     * Shows a process step as a popup.
     */
    private void showAsPopup(){

        initGui();
        dialog.setTitle(title);
        dialog.setContentPane(mainPanel);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}
