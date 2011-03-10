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
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.funambol.admin.util.Log;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.main.Sync4jClassLoader;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.email.admin.Step;

/**
 * This class represent the panel for user searching during insert account
 * process.
 *
 * @version $Id: SearchUserPanel.java,v 1.2 2008-07-17 09:55:50 testa Exp $
 *
 */
public class SearchUserPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    /** search user panel */
    private final FormSearchUserPanel clausesPanel = new FormSearchUserPanel();

    /** search result */
    private ResultSearchUserPanel resultsPanel;

    /**  data access objects */
    private BusinessDelegate businessDelegate;

    /**
     * Object that holds "next step logic" (that is: how the process using
     * this panel goes to the next process step).
     */
    private Step step;

    // -------------------------------------------------------------- Properties

    /** Sets the WSDao property */
    public void setWSDao(WSDAO WSDao) {
        resultsPanel.setWSDao(WSDao);
    }

    /** Gets the selected user */
    public Sync4jUser getUser() {
        return resultsPanel.getSelectedUser();
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of a SearchUserPanel.
     *
     * @param step object that holds "next step logic"
     */
    public SearchUserPanel(Step step) {
        this.step = step;
        try {
            initWS();
            initGui();

        } catch (Exception e) {
            Log.error(e);
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Initializes visual elements.
     *
     * @throws java.lang.Exception
     */
    private void initGui() throws Exception{

        resultsPanel = new ResultSearchUserPanel(this.step);

        setLayout(new BorderLayout());

        JScrollPane topPanel = new JScrollPane(clausesPanel);
        JScrollPane bottomPanel = new JScrollPane(resultsPanel);

        add(topPanel,BorderLayout.PAGE_START);
        add(bottomPanel,BorderLayout.CENTER);

        //
        // Search for all the ds-server users with the given clause:
        //
        // 1 - search all the users that match the given clause
        // 2 - load the results into the result table
        //
        clausesPanel.addFormActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {

                // gets the user list                
                Clause clause = clausesPanel.getClause();
                try {

                    Sync4jUser[] users = businessDelegate.getUsers(clause);
                    resultsPanel.loadUsers(users);

                } catch (Exception e) {
                    Log.error(Log.captureStackTrace(e));
                }
                
                // resets the result table
                resultsPanel.resetResultTableSelection();
            }
        });
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
}
