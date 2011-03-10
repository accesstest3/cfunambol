/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.admin.mailservers;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.funambol.admin.util.ClauseTools;

import com.funambol.email.admin.Constants;
import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.model.MailServer;

/**
 * Panel for specifying criteria for search of public mail servers.
 *
 * @version $Id: FormSearchMailServerPanel.java,v 1.1 2008-03-25 11:28:21 gbmiglia Exp $
 */
public class FormSearchMailServerPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    /** column upon which user search filtering applies */
    private static String PARAM_DESCRIPTION = "description";

    // ------------------------------------------------------------ Private data

    //
    // Visual elements.
    //

    /** label for the panel's name  */
    private JLabel jLPanelName = new JLabel();

    private JLabel      jLDescription       = new JLabel();
    private JComboBox   jCBoxDescription    = GuiFactory.getDefaultComboSearch();
    private JTextField  jTFDescription      = new JTextField();

    /** search button */
    private JButton jBSearch = new JButton();

    /** reset button */
    private JButton jBReset = new JButton();

    // -------------------------------------------------------------- Properties

    /** data access object */
    private WSDAO WSDao;

    /**
     * Sets the WSDao property.
     */
    public void setWSDao(WSDAO WSDao){
        this.WSDao = WSDao;
    }

    /** searched mail servers */
    private MailServer[] mailServers;

    /**
     * Gets the mailservers property.
     */
    public MailServer[] getMailServers(){
        return mailServers;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of FormSearchMailServerPanel */
    public FormSearchMailServerPanel() {
        initGui();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Initializes visual elements.
     */
    private void initGui(){

        setLayout(null);

        //
        // Configures components.
        //

        jLPanelName.setFont(GuiFactory.titlePanelFont);
        jLPanelName.setText("Public Mail Servers");
        jLPanelName.setBounds(new Rectangle(0, 5, 316, 28));
        jLPanelName.setAlignmentX(SwingConstants.CENTER);

        TitledBorder titledBorder = new TitledBorder("");
        jLPanelName.setBorder(titledBorder);

        jLDescription.setText("Server description");
        jLDescription.setBounds(new Rectangle(0, 53, 90, 20));
        jCBoxDescription.setBounds(new Rectangle(98, 53, 92, 20));
        jTFDescription.setBounds(new Rectangle(198, 53, 151, 20));

        jBSearch.setBounds(new Rectangle(467, 53, 88, 23));
        jBSearch.setText("Search");
        jBSearch.setActionCommand(Constants.ACTION_COMMAND_SEARCH_MAILSERVER);

        jBReset.setBounds(new Rectangle(367, 53, 88, 23));
        jBReset.setText("Reset");
        jBReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reset();
            }
        });

        jLDescription.setFont(GuiFactory.defaultFont);
        jCBoxDescription.setFont(GuiFactory.defaultFont);
        jTFDescription.setFont(GuiFactory.defaultFont);
        jBReset.setFont(GuiFactory.defaultFont);
        jBSearch.setFont(GuiFactory.defaultFont);

        //
        // Adds components to this panel.
        //

        add(jLPanelName);
        add(jLDescription);
        add(jCBoxDescription);
        add(jTFDescription);
        add(jBReset);
        add(jBSearch);
    }

    /**
     * Resets panel fields.
     */
    private void reset() {

        jCBoxDescription.setSelectedIndex(0);
        jTFDescription.setText("");
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets field values and create a clause with these parameters.
     * @return Clause
     */
    public Clause getClause() {

        Clause clause = null;

        // edited value
        String description = jTFDescription.getText();

        // combo value
        String cmbDescription = (String) (jCBoxDescription.getSelectedItem());

        String[] param = null;
        String[] value = null;
        String[] operator = null;

        ArrayList paramList = new ArrayList();
        ArrayList valueList = new ArrayList();
        ArrayList operatorList = new ArrayList();

        int i = 0;

        //
        // Checks must be performed on edited text.
        //
        if (description.equalsIgnoreCase("") || description.equalsIgnoreCase(null)) {

        } else {
            paramList.add(PARAM_DESCRIPTION);
            valueList.add(description);
            operatorList.add(ClauseTools.operatorViewToOperatorValue(cmbDescription));
            i++;
        }

        param = (String[])paramList.toArray(new String[0]);
        value = (String[])valueList.toArray(new String[0]);
        operator = (String[])operatorList.toArray(new String[0]);

        clause = ClauseTools.createClause(param, operator, value);

        return clause;
    }

    /**
     * Sets the action for the panel search button.
     * @param actionListener
     */
    public void addSearchAction(ActionListener actionListener){
        jBSearch.addActionListener(actionListener);
    }

    // -------------------------------------------------------- Override methods

    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 90);
    }

}
