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

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.LogicalClause;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Log;

/**
 * Panel for specifying criteria for account searching.
 *
 * @version $Id: FormSearchAccountPanel.java,v 1.1 2008-03-25 11:28:17 gbmiglia Exp $
 */
public class FormSearchAccountPanel  extends JPanel {

    // --------------------------------------------------------------- Constants

    /** columns upon which account search filtering applies */

    private static final String PARAM_USER_NAME           = "username";
    private static final String PARAM_SERVER_DESCRIPTION  = "description";
    private static final String PARAM_ACTIVATION          = "active";
    private static final String PARAM_PUSH                = "push";

    /** acivation combo box item */
    private static final String ITEM_ALL = "All";
    private static final String ITEM_ACTIVE = "Active";
    private static final String ITEM_NONACTIVE = "Non active";

    /** push combo box item */
    private static final String ITEM_PUSH_ALL = "All";
    private static final String ITEM_PUSH_ACTIVE = "Active";
    private static final String ITEM_PUSH_NONACTIVE = "Non active";

    // ------------------------------------------------------------ Private data

    //
    // Visual elements
    //

    private JLabel jLUsername = new JLabel();
    private JLabel jLServer = new JLabel();
    private JLabel jLActive = new JLabel();
    private JLabel jLPush = new JLabel();

    private JComboBox jCBoxUsername = GuiFactory.getDefaultComboSearch();
    private JComboBox jCBoxServerDescription = GuiFactory.getDefaultComboSearch();

    private JTextField jTFUsername = new JTextField();
    private JTextField jTFServerDescription = new JTextField();
    private JComboBox jCBoxActive = new JComboBox();
    private JComboBox jCBoxPush = new JComboBox();

    /** label for the panel's name  */
    private JLabel panelName = new JLabel();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** button to search an account */
    private JButton jBSearch = new JButton();

    /** button to reset form */
    private JButton jBReset = new JButton();

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of a FormSearchAccountPanel object.
     */
    public FormSearchAccountPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            Log.error("Error creating Search Account Panel " + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public methods

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 165);
    }

    /**
     * Add the listener received as parameter to the buttons of the panel
     * @param actionListener: the listener of the action
     */
    public void addFormActionListener(ActionListener actionListener) {
        jBSearch.addActionListener(actionListener);
    }

    /**
     * Get textfields' edited values and create a clause with these parameters
     * @return Clause
     */
    public Clause getClause() {

        Clause clause = null;

        // edited value
        String username = jTFUsername.getText();
        String serverDescription = jTFServerDescription.getText();

        // combo value
        String cmbUser              = (String)(jCBoxUsername.getSelectedItem());
        String cmbServerDescription = (String)(jCBoxServerDescription.getSelectedItem());
        String active               = (String)jCBoxActive.getSelectedItem();
        String push                 = (String)jCBoxPush.getSelectedItem();

        String[] param = null;
        String[] value = null;
        String[] operator = null;

        ArrayList paramList = new ArrayList();
        ArrayList valueList = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;
        if (username.equalsIgnoreCase("") || username.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_USER_NAME);
            valueList.add(username);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbUser));
            i++;
        }
        if (serverDescription.equalsIgnoreCase("") || serverDescription.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_SERVER_DESCRIPTION);
            valueList.add(serverDescription);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbServerDescription));
            i++;
        }

        // combo: activation
        String activationValue = null;
        String activationFieldValue = (String)jCBoxActive.getSelectedItem();
        if(activationFieldValue == ITEM_ACTIVE) {
            activationValue = "y";
        } else if (activationFieldValue == ITEM_NONACTIVE){
            activationValue = "n";
        } else if (activationFieldValue == ITEM_ALL) {
            activationValue = null;
        }

        Clause activationClause = null;
        if(activationValue != null){
            paramList.add(PARAM_ACTIVATION);
            valueList.add(activationValue);
            operatorList.add(ClauseTools.operatorViewToOperatorValue("Exactly"));
        } else {
            activationClause = new AllClause();
        }


        // combo: push
        String pushValue = null;
        String pushFieldValue = (String)jCBoxPush.getSelectedItem();
        if(pushFieldValue == ITEM_PUSH_ACTIVE) {
            pushValue = "y";
        } else if (pushFieldValue == ITEM_PUSH_NONACTIVE){
            pushValue = "n";
        } else if (pushFieldValue == ITEM_PUSH_ALL) {
            pushValue = null;
        }

        Clause pushClause = null;
        if(pushValue != null){
            paramList.add(PARAM_PUSH);
            valueList.add(pushValue);
            operatorList.add(ClauseTools.operatorViewToOperatorValue("Exactly"));
        } else {
            pushClause = new AllClause();
        }


        param    = (String[])paramList.toArray(new String[0]);
        value    = (String[])valueList.toArray(new String[0]);
        operator = (String[])operatorList.toArray(new String[0]);

        clause = ClauseTools.createClause(param, operator, value);

        if (activationClause != null && !(activationClause instanceof AllClause)){
            Clause[] clauses = {activationClause, clause};
            clause = new LogicalClause(LogicalClause.OPT_AND, clauses);
        }

        if (pushClause != null && !(pushClause instanceof AllClause)){
            Clause[] clauses = {pushClause, clause};
            clause = new LogicalClause(LogicalClause.OPT_AND, clauses);
        }

        return clause;
    }

    // --------------------------------------------------------- Private methods
    /**
     * Initializes visual elements.
     *
     * @throws Exception if error occures during creation of the panel
     */
    private void jbInit() throws Exception {

        this.setLayout(null);

        titledBorder1 = new TitledBorder("");
        this.setBorder(BorderFactory.createEmptyBorder());
        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText("Accounts");
        panelName.setBounds(new Rectangle(0, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        MutableComboBoxModel jCBoxActiveModel = new DefaultComboBoxModel();
        jCBoxActiveModel.addElement(ITEM_ALL);
        jCBoxActiveModel.addElement(ITEM_ACTIVE);
        jCBoxActiveModel.addElement(ITEM_NONACTIVE);
        jCBoxActive.setModel(jCBoxActiveModel);

        MutableComboBoxModel jCBoxPushModel = new DefaultComboBoxModel();
        jCBoxPushModel.addElement(ITEM_PUSH_ALL);
        jCBoxPushModel.addElement(ITEM_PUSH_ACTIVE);
        jCBoxPushModel.addElement(ITEM_PUSH_NONACTIVE);
        jCBoxPush.setModel(jCBoxPushModel);

        jLUsername.setText("Username");
        jLServer.setText("Server description");
        jLActive.setText("Activation");
        jLPush.setText("Push");

        jLUsername.setBounds(new Rectangle(0, 53,  76, 20));
        jLServer.setBounds  (new Rectangle(0, 78,  90, 20));
        jLActive.setBounds  (new Rectangle(0, 103, 76, 20));
        jLPush.setBounds    (new Rectangle(0, 128, 76, 20));

        jCBoxUsername.setBounds         (new Rectangle(98, 53,  92, 20));
        jCBoxServerDescription.setBounds(new Rectangle(98, 78,  92, 20));
        jCBoxActive.setBounds           (new Rectangle(98, 103, 92, 20));
        jCBoxPush.setBounds             (new Rectangle(98, 128, 92, 20));

        jTFUsername.setBounds         (new Rectangle(198, 53, 151, 20));
        jTFServerDescription.setBounds(new Rectangle(198, 78, 151, 20));

        jBSearch.setBounds(new Rectangle(467, 53, 88, 23));
        jBSearch.setText("Search");
        jBSearch.setActionCommand("ACTION_COMMAND_SEARCH_ACCOUNT");

        jBReset.setBounds(new Rectangle(367, 53, 88, 23));
        jBReset.setText("Reset");
        jBReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reset();
            }
        });

        jLUsername.setFont(GuiFactory.defaultFont);
        jLServer.setFont(GuiFactory.defaultFont);
        jLActive.setFont(GuiFactory.defaultFont);
        jLPush.setFont(GuiFactory.defaultFont);

        jCBoxUsername.setFont(GuiFactory.defaultFont);
        jCBoxServerDescription.setFont(GuiFactory.defaultFont);
        jTFUsername.setFont(GuiFactory.defaultFont);
        jTFServerDescription.setFont(GuiFactory.defaultFont);

        jCBoxActive.setFont(GuiFactory.defaultFont);
        jCBoxPush.setFont(GuiFactory.defaultFont);

        jBSearch.setFont(GuiFactory.defaultFont);
        jBReset.setFont(GuiFactory.defaultFont);

        add(panelName , null);
        add(jLUsername, null);
        add(jLServer, null);
        add(jLActive, null);
        add(jLPush, null);
        add(jCBoxUsername, null);
        add(jCBoxServerDescription, null);
        add(jTFUsername, null);
        add(jTFServerDescription, null);
        add(jCBoxActive, null);
        add(jCBoxPush, null);
        add(jBSearch, null);
        add(jBReset, null);
    }

    /**
     * Clears panel fields.
     */
    private void reset() {

        jCBoxUsername.setSelectedIndex(0);
        jCBoxServerDescription.setSelectedIndex(0);
        jCBoxActive.setSelectedIndex(0);
        jCBoxPush.setSelectedIndex(0);

        jTFUsername.setText("");
        jTFServerDescription.setText("");
    }
}
