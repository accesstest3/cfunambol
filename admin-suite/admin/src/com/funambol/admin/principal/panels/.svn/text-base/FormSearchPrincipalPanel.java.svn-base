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

import java.awt.Color;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.funambol.framework.filter.Clause;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.ClauseTools;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Create the panel used for searching principal.<br>
 * The panel includes all the fields associated to a principal:
 * principalID, username, deviceid.
 *
 * @version $Id: FormSearchPrincipalPanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 **/
public class FormSearchPrincipalPanel extends JPanel {
    // --------------------------------------------------------------- Constants

    /** constant for search query identifing username */
    private static String PARAM_USER = "username";

    /** constant for search query identifing device */
    private static String PARAM_DEVICE = "device";

    /** constant for search query identifing principalid */
    private static String PARAM_PRINCIPAL = "id";

    // ------------------------------------------------------------ Private data
    /** label for the panel's name  */
    private JLabel panelName = new JLabel();

    /** label for user */
    private JLabel jLUser = new JLabel();

    /** label for device */
    private JLabel jLDevice = new JLabel();

    /** label for principal */
    private JLabel jLPrincipal = new JLabel();

    /** combobox for user parameters in query */
    private JComboBox jCBUser = GuiFactory.getDefaultComboSearch();

    /** combobox for device parameters in query */
    private JComboBox jCBDevice = GuiFactory.getDefaultComboSearch();

    /** combobox for principal parameters in query */
    private JComboBox jCBPrincipal = GuiFactory.getDefaultComboSearch();

    /** textfield for editing username */
    private JTextField jTFUser = new JTextField();

    /** textfield for editing deviceid */
    private JTextField jTFDevice = new JTextField();

    /** textfield for editing principalid */
    private JTextField jTFPrincipal = new JTextField();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** button to search a principal */
    private JButton jBSearch = new JButton();

    /** button to reset form */
    private JButton jBReset = new JButton();

    /** border for title */
    private Border border1;

    // ------------------------------------------------------------ Constructors
    /**
     * Generate the panel
     */
    public FormSearchPrincipalPanel() {
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
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);
    }

    /**
     * Add the listener recived as parameter to the buttons of the panel
     * @param actionListener : the listener of the action
     */
    public void addFormActionListener(ActionListener actionListener) {
        jBSearch.addActionListener(actionListener);
    }

    /**
     * Get textfields' edited values and create a clause with these parameters
     * @return Clause: array of the clause
     */
    public Clause getClause() {

        Clause clause = null;

        // edited value
        String principal = jTFPrincipal.getText();
        String username = jTFUser.getText();
        String device = jTFDevice.getText();
        // combo value
        String cmbprincipal = (String) (jCBPrincipal.getSelectedItem());
        String cmbuser = (String) (jCBUser.getSelectedItem());
        String cmbdevice = (String) (jCBDevice.getSelectedItem());

        String[] param = null;
        String[] value = null;
        String[] operator = null;

        ArrayList paramList = new ArrayList();
        ArrayList valueList = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;
        // controlli su testo editato !
        if (principal.equalsIgnoreCase("") || principal.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_PRINCIPAL);
            valueList.add(principal);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbprincipal));
            i++;
        }
        if (username.equalsIgnoreCase("") || username.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_USER);
            valueList.add(username);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbuser));
            i++;
        }
        if (device.equalsIgnoreCase("") || device.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_DEVICE);
            valueList.add(device);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbdevice));
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
    private void init() throws Exception {
        // set layout
        this.setLayout(null);

        // set properties of label, position and border
        // referred to the title of the panel
        titledBorder1 = new TitledBorder("");
        border1 = BorderFactory.createLineBorder(Color.black, 1);

        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.SEARCH_PRINCIPAL_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        // set properties of label and textfield referred to principalID including position
        jLPrincipal.setBounds(new Rectangle(13, 53, 76, 20));
        jLPrincipal.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_PRINCIPALID) + " :");
        jCBPrincipal.setBounds(new Rectangle(91, 53, 92, 20));

        jTFPrincipal.setBounds(new Rectangle(191, 53, 151, 20));

        // set properties of label and textfield referred to username including position
        jLUser.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_USERNAME) + " :");
        jLUser.setBounds(new Rectangle(13, 78, 76, 20));
        jCBUser.setBounds(new Rectangle(91, 78, 92, 20));

        jTFUser.setBounds(new Rectangle(191, 78, 151, 20));

        // set properties of label and textfield referred to deviceid including position
        jLDevice.setBounds(new Rectangle(13, 103, 76, 20));
        jLDevice.setText(Bundle.getMessage(Bundle.PRINCIPAL_PANEL_DEVICEID) + " :");
        jCBDevice.setBounds(new Rectangle(91, 103, 92, 20));

        jTFDevice.setBounds(new Rectangle(191, 103, 151, 20));

        // set position of button and his label
        jBSearch.setBounds(new Rectangle(195, 165, 88, 23));
        jBSearch.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SEARCH));
        jBSearch.setActionCommand(Constants.ACTION_COMMAND_SEARCH_PRINCIPAL);

        jBReset.setBounds(new Rectangle(76, 165, 88, 23));
        jBReset.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_RESET));
        jBReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reset();
            }
        });

        jLUser.setFont(GuiFactory.defaultFont);
        jLDevice.setFont(GuiFactory.defaultFont);
        jLPrincipal.setFont(GuiFactory.defaultFont);
        jCBUser.setFont(GuiFactory.defaultFont);
        jCBDevice.setFont(GuiFactory.defaultFont);
        jCBPrincipal.setFont(GuiFactory.defaultFont);
        jTFUser.setFont(GuiFactory.defaultFont);
        jTFDevice.setFont(GuiFactory.defaultFont);
        jTFPrincipal.setFont(GuiFactory.defaultFont);
        jBSearch.setFont(GuiFactory.defaultFont);
        jBReset.setFont(GuiFactory.defaultFont);

        // add all components to the panel
        this.add(panelName   , null);
        this.add(jLPrincipal , null);        
        this.add(jCBPrincipal, null);
        this.add(jTFPrincipal, null);
        this.add(jLUser      , null);
        this.add(jCBUser     , null);
        this.add(jTFUser     , null);
        this.add(jLDevice    , null);
        this.add(jCBDevice   , null);
        this.add(jTFDevice   , null);        
        this.add(jBSearch    , null);
        this.add(jBReset     , null);
    }

    /**
     * Clear value in the panel
     */
    private void reset() {
        jCBDevice.setSelectedIndex(0);
        jCBPrincipal.setSelectedIndex(0);
        jCBUser.setSelectedIndex(0);

        jTFDevice.setText("");
        jTFPrincipal.setText("");
        jTFUser.setText("");
    }

}
