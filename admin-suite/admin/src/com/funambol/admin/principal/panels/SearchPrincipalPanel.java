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

import java.text.MessageFormat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.security.Sync4jPrincipal;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Create a panel to search principal
 *
 * @version $Id: SearchPrincipalPanel.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 **/

public class SearchPrincipalPanel extends JPanel implements ActionListener, ListSelectionListener {

    // ------------------------------------------------------------ Private data
    /** Split Panel .. */
    private JSplitPane splitPanel = null;

    /** Panel .. */
    private FormSearchPrincipalPanel formSearchPrincipalPanel = null;

    /** Panel .. */
    private JPanel resultSearchPrincipalPanel = null;

    /** Button .. */
    private ButtonResultSearchPrincipalPanel buttonSearchPrincipalPanel = null;

    /** Panel .. */
    private ResultSearchPrincipalPanel tableResultSearchPrincipalPanel = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Generate the panel
     */
    public SearchPrincipalPanel() {

        this.setLayout(new BorderLayout());
        this.setName(Bundle.getMessage(Bundle.SEARCH_PRINCIPAL_PANEL_NAME));

        formSearchPrincipalPanel = new FormSearchPrincipalPanel();
        tableResultSearchPrincipalPanel = new ResultSearchPrincipalPanel();
        buttonSearchPrincipalPanel = new ButtonResultSearchPrincipalPanel();

        formSearchPrincipalPanel.addFormActionListener(this);
        tableResultSearchPrincipalPanel.addListSelectionListenerToTable(this);
        buttonSearchPrincipalPanel.addButtonActionListener(this);

        resultSearchPrincipalPanel = new JPanel();
        resultSearchPrincipalPanel.setLayout(new BorderLayout());
        resultSearchPrincipalPanel.add(tableResultSearchPrincipalPanel, BorderLayout.CENTER);
        resultSearchPrincipalPanel.add(buttonSearchPrincipalPanel, BorderLayout.EAST);

        JScrollPane formPanel = new JScrollPane(formSearchPrincipalPanel);
        formPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane resultPanel = new JScrollPane(resultSearchPrincipalPanel);
        resultPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultPanel.setBorder(BorderFactory.createEtchedBorder());

        splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, formPanel, resultPanel);
        splitPanel.setDividerSize(0);
        splitPanel.setDividerLocation(210);
        splitPanel.setBorder(BorderFactory.createEmptyBorder());

        this.add(splitPanel, BorderLayout.CENTER);

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Loads Principal in the table
     * @param principals Sync4jPrincipal[]
     */
    public void loadPrincipals(Sync4jPrincipal[] principals) {
        tableResultSearchPrincipalPanel.loadPrincipals(principals);
        if (principals != null && principals.length > 0) {
            buttonSearchPrincipalPanel.enableButtons(true);
        } else {
            buttonSearchPrincipalPanel.enableButtons(false);
        }        
    }

    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed. Cases are:<br>
     * 1- delete : delete principal with the selected values <br>
     * 2- search : search principal with form parameters and
     *  display results in user result table.<br>
     * 3- insert : insert principal.<br>
     * @param event ActionEvent
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Log.debug("Process " + command + " command");

        String description = null;
        try {
            if (command.equals(Constants.ACTION_COMMAND_DELETE_PRINCIPAL)) {
                description = Bundle.getMessage(Bundle.DELETING);
                if (tableResultSearchPrincipalPanel.getTable().getSelectedRow() == -1) {
                    NotifyDescriptor desc = new NotifyDescriptor.Message(
                        Bundle.getMessage(Bundle.NO_DATA_SELECTED));
                    DialogDisplayer.getDefault().notify(desc);
                } else {
                    Sync4jPrincipal principalToDelete = tableResultSearchPrincipalPanel.
                        getPrincipalToDelete();

                    boolean principalDeleted = false;

                    principalDeleted = ExplorerUtil.getSyncAdminController()
                                                   .getPrincipalsController()
                                                   .deletePrincipal(principalToDelete);
                    if (principalDeleted) {
                        int row = tableResultSearchPrincipalPanel.getTable().getSelectedRow();
                        tableResultSearchPrincipalPanel.deleteRowInTable(row);
                    }
                }

            } else if (command.equals(Constants.ACTION_COMMAND_SEARCH_PRINCIPAL)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                Clause clause = formSearchPrincipalPanel.getClause();

                ExplorerUtil.getSyncAdminController()
                            .getPrincipalsController()
                            .searchPrincipals(clause);
            } else if (command.equals(Constants.ACTION_COMMAND_ADD_PRINCIPAL)) {
                description = Bundle.getMessage(Bundle.ADDING);

                ExplorerUtil.getSyncAdminController()
                            .getPrincipalsController()
                            .startNewPrincipalProcess();
            } else if (command.equals(Constants.ACTION_COMMAND_SHOW_SYNC_DETAILS)) {
                description = Bundle.getMessage(Bundle.SEARCHING);

                try {
                    ExplorerUtil.getSyncAdminController()
                                .getPrincipalsController()
                                .startLastTimestampsProcess(tableResultSearchPrincipalPanel.getSelectedPrincipal());                

                } catch (AdminException e) {
                    Log.error(
                        MessageFormat.format(
                            Bundle.getMessage(Bundle.ERROR_HANDLING_LAST_TIMESTAMP),
                            new String[] { description }
                        )
                    );
                }                

            }
        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_PRINCIPAL),
                    new String[] { description }
                )
            );
        }
    }

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Enebles buttons if valued is changed
     * @param event ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent event) {
        int numRowsSelected = tableResultSearchPrincipalPanel.getNumRowsSelected();
        if (numRowsSelected > 0) {
            buttonSearchPrincipalPanel.enableButtons(true);
        } else {
            buttonSearchPrincipalPanel.enableButtons(false);
        }
    }

}
