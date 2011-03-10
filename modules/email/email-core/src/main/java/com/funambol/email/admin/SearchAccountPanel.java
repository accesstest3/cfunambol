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

import com.funambol.admin.AdminException;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.main.Sync4jClassLoader;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.server.Sync4jUser;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.funambol.framework.filter.Clause;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Log;

import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.model.MailServerAccount;
import com.funambol.server.admin.ws.client.ServerInformation;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This is the panel associated with the admin tool node for the email connector
 * module.
 *
 * @version $Id: SearchAccountPanel.java,v 1.3 2008-05-18 15:06:36 nichele Exp $
 */
public class SearchAccountPanel extends JPanel implements ComponentObserver {
    
    // ------------------------------------------------------------ Private data
    
    /** data access object */
    private WSDAO WSDao;
    
    /** data access objects to access ds server */
    private BusinessDelegate businessDelegate;
    
    /** clause for filtered search */
    private Clause clause;
    
    /** controller that menages account processes */
    private AccountController accountController;
    
    /** search clauses */
    private FormSearchAccountPanel formSearchAccountPanel;
    
    /** search results */
    private ResultSearchAccountPanel resultSearchAccountPanel;
    
    /** at least one account search has been performed or not */
    private boolean firstSearchDone = false;
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * Creates a new instance of a SearchAccountPanel object. A reference to an
     * AccountController object is passed to it, in order to manage account
     * processing.
     *
     * @param accountController the AccountController object reference
     */
    public SearchAccountPanel(AccountController accountController){
        this.accountController = accountController;
        try {
            initWSDAO();
            initWS();
            initGui();
            
        } catch (Exception e) {
            Log.error(e);
        }
    }
    
    
    
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 550);
    }
    
    // --------------------------------------------------------- Private methods
    
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
     * Initializes visual elements.
     */
    private void initGui() {
        
        //
        // search clauses
        //
        formSearchAccountPanel = new FormSearchAccountPanel();
        
        formSearchAccountPanel.addFormActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                // determine which button has been pressed
                String command = event.getActionCommand();
                
                //
                // search button pressed
                //
                if (command == Constants.ACTION_COMMAND_SEARCH_ACCOUNT){
                    
                    clause = formSearchAccountPanel.getClause();
                    
                    try {
                        
                        MailServerAccount[] displaiedAccount = WSDao.getAccounts(clause);
                        resultSearchAccountPanel.loadAccounts(displaiedAccount);
                        
                    } catch (Exception e) {
                        Log.error(e);
                    }
                    
                    if (!firstSearchDone){
                        firstSearchDone = true;
                    }
                }
                
            }
        });
        
        //
        // search results
        //
        resultSearchAccountPanel = new ResultSearchAccountPanel();
        
        setLayout(new BorderLayout());
        add(formSearchAccountPanel,BorderLayout.PAGE_START);
        add(resultSearchAccountPanel,BorderLayout.CENTER);
        
        resultSearchAccountPanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                // determine which button has been pressed
                String command = event.getActionCommand();
                String description = null;
                try {
                    accountController.setParentFrame(getOwner());
                    if (command.equals(Constants.ACTION_COMMAND_ADD_ACCOUNT)){
                        accountController.selectUser();
                    } else
                        if (command.equals(Constants.ACTION_COMMAND_EDIT_ACCOUNT)){
                        modifyAccount();
                        } else
                            if (command.equals(Constants.ACTION_COMMAND_DELETE_ACCOUNT)){
                        
                        MailServerAccount selectedAccount = resultSearchAccountPanel.getSelectedAccount();
                        if (selectedAccount == null){
                            return;
                        }
                        
                        int numberRowsDeleted = accountController.deleteAccount(selectedAccount);
                        
                        if (numberRowsDeleted > 0){
                            
                            // update the search account result table, if not empty
                            resultSearchAccountPanel.deleteRowInTable();
                        }
                            } else
                                if (command.equals(Constants.ACTION_COMMAND_SHOW_CACHE)){
                        
                        MailServerAccount selectedAccount =
                                resultSearchAccountPanel.getSelectedAccount();
                        accountController.showCache(
                                selectedAccount.getUsername(),
                                selectedAccount.getMailServer().getProtocol());
                                }
                    
                } catch (Exception e) {
                    Log.error("Error handling accounts", e);
                }
            }
        });
        
        resultSearchAccountPanel.setRowMouseAction(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    modifyAccount();
                }
            }
        });
    }
    
    
    
    /**
     * Updates the search result table. View upadate applies only if at least
     * one search has been performed (that is: flag firstSearchDone is true).
     */
    public void update() {
        
        if (!firstSearchDone){
            return;
        }
        
        Clause clause = formSearchAccountPanel.getClause();
        
        clause = formSearchAccountPanel.getClause();
        
        try {
            
            MailServerAccount[] displaiedAccount = WSDao.getAccounts(clause);
            resultSearchAccountPanel.loadAccounts(displaiedAccount);
            
        } catch (Exception e) {
            Log.error(e);
        }
        
    }
    
    // --------------------------------------------------------- Private methods
    
    /*
     * Returns the frame owner of this panel
     * @return Frame
     */
    private Frame getOwner() {
        Container parent = this;
        while ( (parent = parent.getParent()) != null) {
            if (parent instanceof Frame) {
                return (Frame)parent;
            }
        }
        return null;
    }

    /**
     * Initialize businessDelegate object.
     *
     * @throws java.lang.Exception
     */
    private void initWS() throws Exception{
        AdminConfig config = AdminConfig.getAdminConfig();

        Sync4jClassLoader cl = new Sync4jClassLoader(config.getSync4jCodeBase(), getClass().getClassLoader());
        businessDelegate = new BusinessDelegate();
        businessDelegate.init(cl);
        businessDelegate.login();
    }
    
    /**
     * Action performed as modification of a mail server account is requested.
     */
    private void modifyAccount(){
        //
        // 1 - get selected account
        // 2 - check if the ds user bound to the selected account is still in the
        //     db. If it does not longer exist then exit
        // 3 - modify account, if user is still present
        //
        MailServerAccount selectedAccount = resultSearchAccountPanel.getSelectedAccount();
        
        Sync4jUser[] users = null;
        boolean exceptionOccured = false;
        String username = selectedAccount.getUsername();

        try {                     
            WhereClause wc = new WhereClause(
                    "username", new String[] {username}, WhereClause.OPT_EQ, true);
            Clause userClause = ClauseTools.createClause(
                    new String[] {"username"},
                    new String[] {username},
                    new String[] {WhereClause.OPT_EQ});
            
            users = businessDelegate.getUsers(wc);
        } catch (AdminException ex) {
            exceptionOccured = true;
        }

        //
        // If user does not exist, or an esception occurred, then a message is
        // displaied and then exits.
        //
        if (users == null || users.length == 0 || exceptionOccured == true){
            
            JOptionPane.showMessageDialog(this,
                    "User '" + username + "' does not longer exist.");
            return;
        }
        
        
        // update the search account result table, if not empty
        
        MailServerAccount account = accountController.modifyAccount(selectedAccount);
        resultSearchAccountPanel.updateRowInTable(account);
    }
    
}
