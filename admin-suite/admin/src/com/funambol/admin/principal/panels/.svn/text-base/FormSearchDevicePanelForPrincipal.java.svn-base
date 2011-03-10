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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
 * Create the panel used for searching device in <code>CreatePrincipalPanel</code>.<br>
 * The panel includes all the fields associated to a device:
 * deviceID, Type, Description.
 *
 * @version $Id: FormSearchDevicePanelForPrincipal.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 **/
public class FormSearchDevicePanelForPrincipal extends JPanel {

    // --------------------------------------------------------------- Constants

    /** constant for search query identifing device_id */
    private static String PARAM_ID = "id";

    /** constant for search query identifing type */
    private static String PARAM_TYPE = "type";

    /** constant for search query identifing description */
    private static String PARAM_DESCRIPTION = "description";

    // ------------------------------------------------------------ Private data
    /** label for the panel's name  */
    private JLabel panelName = new JLabel();

    /** label for id */
    private JLabel jLId = new JLabel();

    /** label for description */
    private JLabel jLDescription = new JLabel();

    /** label for type */
    private JLabel jLType = new JLabel();

    /** combobox for id parameters in query */
    private JComboBox jCBId = GuiFactory.getDefaultComboSearch();

    /** combobox for description parameters in query */
    private JComboBox jCBDescription = GuiFactory.getDefaultComboSearch();

    /** combobox for type parameters in query */
    private JComboBox jCBType = GuiFactory.getDefaultComboSearch();

    /** textfield for editing id */
    private JTextField jTFId = new JTextField();

    /** textfield for editing description */
    private JTextField jTFDescription = new JTextField();

    /** textfield for editing type */
    private JTextField jTFType = new JTextField();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** button to search a device */
    private JButton jBSearch = new JButton();

    /** button to reset form */
    private JButton jBReset = new JButton();

    // ------------------------------------------------------------ Constructors
    /**
     * Generate the panel
     */
    public FormSearchDevicePanelForPrincipal() {

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
    public void addFormDeviceActionListener(ActionListener actionListener) {
        jBSearch.addActionListener(actionListener);
    }

    /**
     * Get textfields' edited values and create a clause with these parameters
     * @return Clause
     */
    public Clause getClause() {

        Clause clause = null;

        // edited value
        String id = jTFId.getText();
        String type = jTFType.getText();
        String description = jTFDescription.getText();
        // combo value
        String cmbid = (String) (jCBId.getSelectedItem());
        String cmbtype = (String) (jCBType.getSelectedItem());
        String cmbdescr = (String) (jCBDescription.getSelectedItem());

        String[] param = null;
        String[] value = null;
        String[] operator = null;

        ArrayList paramList = new ArrayList();
        ArrayList valueList = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;
        // check about edited text
        if (id.equalsIgnoreCase("") || id.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_ID);
            valueList.add(id);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbid));
            i++;
        }
        if (type.equalsIgnoreCase("") || type.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_TYPE);
            valueList.add(type);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbtype));
            i++;
        }
        if (description.equalsIgnoreCase("") || description.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_DESCRIPTION);
            valueList.add(description);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbdescr));
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
        //  referred to the title of the panel
        titledBorder1 = new TitledBorder("");

        panelName.setFont(new java.awt.Font("Arial", 2, 12));
        panelName.setText(Bundle.getMessage(Bundle.SEARCH_DEVICE_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 5, 162, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        // set properties of label and textfield referred to ID including position
        jLId.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_ID) + " :");
        jLId.setBounds(new Rectangle(13, 53, 76, 20));
        jCBId.setBounds(new Rectangle(91, 53, 92, 20));

        jTFId.setBounds(new Rectangle(191, 53, 151, 20));

        // set properties of label and textfield referred to description including position
        jLDescription.setBounds(new Rectangle(13, 78, 76, 20));
        jLDescription.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_TYPE) + " :");
        jCBDescription.setBounds(new Rectangle(91, 78, 92, 20));

        jTFDescription.setBounds(new Rectangle(191, 78, 151, 20));

        // set properties of label and textfield referred to TYPE including position
        jLType.setBounds(new Rectangle(13, 103, 76, 20));
        jLType.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_DESCRIPTION) + " :");
        jCBType.setBounds(new Rectangle(91, 103, 92, 20));

        jTFType.setBounds(new Rectangle(191, 103, 151, 20));

        // set position of button and his label
        jBSearch.setBounds(new Rectangle(195, 165, 88, 23));
        jBSearch.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SEARCH));
        jBSearch.setActionCommand(Constants.ACTION_COMMAND_SEARCH_DEVICE);

        jBReset.setBounds(new Rectangle(76, 165, 88, 23));
        jBReset.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_RESET));
        jBReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reset();
            }
        });

        jLId.setFont(GuiFactory.defaultFont);
        jLDescription.setFont(GuiFactory.defaultFont);
        jLType.setFont(GuiFactory.defaultFont);
        jCBId.setFont(GuiFactory.defaultFont);
        jCBDescription.setFont(GuiFactory.defaultFont);
        jCBType.setFont(GuiFactory.defaultFont);
        jTFId.setFont(GuiFactory.defaultFont);
        jTFDescription.setFont(GuiFactory.defaultFont);
        jTFType.setFont(GuiFactory.defaultFont);
        jBSearch.setFont(GuiFactory.defaultFont);
        jBReset.setFont(GuiFactory.defaultFont);

        // add all components to the panel
        this.add(panelName, null);
        this.add(jLId, null);
        this.add(jLDescription, null);
        this.add(jLType, null);
        this.add(jCBId, null);
        this.add(jTFId, null);
        this.add(jCBDescription, null);
        this.add(jTFDescription, null);
        this.add(jCBType, null);               
        this.add(jTFType, null);
        this.add(jBSearch, null);
        this.add(jBReset, null);
    }

    /**
     * Clear value in the panel
     */
    private void reset() {
        jCBDescription.setSelectedIndex(0);
        jCBId.setSelectedIndex(0);
        jCBType.setSelectedIndex(0);

        jTFDescription.setText("");
        jTFId.setText("");
        jTFType.setText("");
    }

}
