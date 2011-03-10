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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.framework.filter.Clause;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Create the panel used for searching Users.<br>
 * The panel includes the fields associated to a user:
 * userName, firstname, lastname, email, ecc.
 *
 * @version $Id: FormSearchUserPanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 **/
public class FormSearchUserPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    private static String PARAM_USER_NAME = "username";
    private static String PARAM_FIRST_NAME = "first_name";
    private static String PARAM_LAST_NAME = "last_name";
    private static String PARAM_EMAIL = "email";

    // ------------------------------------------------------------ Private data

    /** label for the panel's name  */
    private JLabel panelName = new JLabel();

    /** label for user */
    private JLabel jLUser = new JLabel();

    /** label for first name */
    private JLabel jLFirst = new JLabel();

    /** label for last name */
    private JLabel jLLast = new JLabel();

    /** label for email */
    private JLabel jLEmail = new JLabel();

    /** combobox for user parameters in query */
    private JComboBox jCBUser = GuiFactory.getDefaultComboSearch();

    /** combobox for firstname parameters in query */
    private JComboBox jCBFirst = GuiFactory.getDefaultComboSearch();

    /** combobox for secondname parameters in query */
    private JComboBox jCBLast = GuiFactory.getDefaultComboSearch();

    /** combobox for mail parameters in query */
    private JComboBox jCBEmail = GuiFactory.getDefaultComboSearch();

    /** textfield for editing user */
    private JTextField jTFUser = new JTextField();

    /** textfield for editing firstName */
    private JTextField jTFFirst = new JTextField();

    /** textfield for editing lastName */
    private JTextField jTFLast = new JTextField();

    /** textfield for editing email */
    private JTextField jTFEmail = new JTextField();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** button to search a user */
    private JButton jBSearch = new JButton();

    /** button to reset form */
    JButton jBReset = new JButton();

    // ------------------------------------------------------------ Constructors

    /**
     * Generate the panel
     */
    public FormSearchUserPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
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
        return new Dimension(350, 190);
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
        String username = jTFUser.getText();
        String firstname = jTFFirst.getText();
        String lastname = jTFLast.getText();
        String email = jTFEmail.getText();
        // combo value
        String cmbuser = (String) (jCBUser.getSelectedItem());
        String cmbfirst = (String) (jCBFirst.getSelectedItem());
        String cmblast = (String) (jCBLast.getSelectedItem());
        String cmbemail = (String) (jCBEmail.getSelectedItem());

        String[] param = null;
        String[] value = null;
        String[] operator = null;

        ArrayList paramList = new ArrayList();
        ArrayList valueList = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;
        // controlli su testo editato !
        if (username.equalsIgnoreCase("") || username.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_USER_NAME);
            valueList.add(username);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbuser));
            i++;
        }
        if (firstname.equalsIgnoreCase("") || firstname.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_FIRST_NAME);
            valueList.add(firstname);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbfirst));
            i++;
        }
        if (lastname.equalsIgnoreCase("") || lastname.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_LAST_NAME);
            valueList.add(lastname);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmblast));
            i++;
        }
        if (email.equalsIgnoreCase("") || email.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_EMAIL);
            valueList.add(email);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbemail));
            i++;
        }

        param = (String[])paramList.toArray(new String[0]);
        value = (String[])valueList.toArray(new String[0]);
        operator = (String[])operatorList.toArray(new String[0]);

        clause = ClauseTools.createClause(param, operator, value);

        return clause;
    }

    // --------------------------------------------------------- Private methods
    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void jbInit() throws Exception {
        this.setLayout(null);
        titledBorder1 = new TitledBorder("");
        this.setBorder(BorderFactory.createEmptyBorder());
        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.SEARCH_USER_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        jLUser.setText(Bundle.getMessage(Bundle.USER_PANEL_USERNAME) + " :");
        jLUser.setBounds(new Rectangle(13, 53, 76, 20));
        jCBUser.setBounds(new Rectangle(91, 53, 92, 20));

        jTFUser.setBounds(new Rectangle(191, 53, 151, 20));

        jLFirst.setBounds(new Rectangle(13, 78, 76, 20));
        jLFirst.setText(Bundle.getMessage(Bundle.USER_PANEL_FIRSTNAME) + " :");
        jCBFirst.setBounds(new Rectangle(91, 78, 92, 20));

        jTFFirst.setBounds(new Rectangle(191, 78, 151, 20));

        jLLast.setBounds(new Rectangle(13, 103, 76, 20));
        jLLast.setText(Bundle.getMessage(Bundle.USER_PANEL_LASTNAME) + " :");
        jCBLast.setBounds(new Rectangle(91, 103, 92, 20));

        jTFLast.setBounds(new Rectangle(191, 103, 151, 20));

        jLEmail.setBounds(new Rectangle(13, 129, 76, 20));
        jLEmail.setText(Bundle.getMessage(Bundle.USER_PANEL_EMAIL) + " :");
        jCBEmail.setBounds(new Rectangle(91, 129, 92, 20));

        jTFEmail.setBounds(new Rectangle(191, 129, 151, 20));

        jBSearch.setBounds(new Rectangle(195, 165, 88, 23));
        jBSearch.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SEARCH));
        jBSearch.setActionCommand(Constants.ACTION_COMMAND_SEARCH_USER);

        jBReset.setBounds(new Rectangle(76, 165, 88, 23));
        jBReset.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_RESET));
        jBReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reset();
            }
        });

        jTFEmail.setFont(GuiFactory.defaultFont);
        jLUser.setFont(GuiFactory.defaultFont);
        jLFirst.setFont(GuiFactory.defaultFont);
        jLLast.setFont(GuiFactory.defaultFont);
        jLEmail.setFont(GuiFactory.defaultFont);
        jCBUser.setFont(GuiFactory.defaultFont);
        jCBFirst.setFont(GuiFactory.defaultFont);
        jCBLast.setFont(GuiFactory.defaultFont);
        jCBEmail.setFont(GuiFactory.defaultFont);
        jTFUser.setFont(GuiFactory.defaultFont);
        jTFFirst.setFont(GuiFactory.defaultFont);
        jTFLast.setFont(GuiFactory.defaultFont);
        jBSearch.setFont(GuiFactory.defaultFont);
        jBReset.setFont(GuiFactory.defaultFont);

        this.add(panelName , null);
        this.add(jLUser    , null);
        this.add(jCBUser   , null);
        this.add(jTFUser   , null);
        this.add(jLFirst   , null);
        this.add(jCBFirst  , null);
        this.add(jTFFirst  , null);
        this.add(jLLast    , null);
        this.add(jCBLast   , null);
        this.add(jTFLast   , null);
        this.add(jLEmail   , null);
        this.add(jCBEmail  , null);        
        this.add(jTFEmail  , null);        
        this.add(jBSearch  , null);
        this.add(jBReset   , null);
    }

    /**
     * clear data into panel
     */
    private void reset() {
        jCBUser.setSelectedIndex(0);
        jCBEmail.setSelectedIndex(0);
        jCBFirst.setSelectedIndex(0);
        jCBLast.setSelectedIndex(0);

        jTFUser.setText("");
        jTFEmail.setText("");
        jTFFirst.setText("");
        jTFLast.setText("");
    }

}
