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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.Serializable;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.util.Log;
import com.funambol.admin.ui.ConnectorManagementPanel;

import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.admin.mailservers.SearchMailServerPanel;
import com.funambol.server.admin.ws.client.ServerInformation;
import javax.swing.JPanel;

/**
 * This class acts as the entry point for the email connector component admin
 * tool.
 *
 * @version $Id: ConsolePanel.java,v 1.1 2008-03-25 11:28:17 gbmiglia Exp $
 */
public class ConsolePanel
        extends ConnectorManagementPanel
        implements Serializable, ActionListener

{

    // ------------------------------------------------------------ Private data

    private SearchAccountPanel searchAccountPanel;
    private SearchMailServerPanel searchMailServerPanel;

    private AccountController accountController;
    private WSDAO WSDao;

    // ------------------------------------------------------------ Constructors

    public ConsolePanel() {
        try {
            initWSDao();
            accountController = new AccountController(true);
            initGui();
            searchMailServerPanel.addObserver(searchAccountPanel);

        } catch (Exception e) {
            Log.error("Error initializing ConsolePanel", e);
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Create the panel.
     */
    private void initGui() {

        //
        // Mail server section.
        //

        searchMailServerPanel = new SearchMailServerPanel();
        searchMailServerPanel.setWSDao(WSDao);
        searchMailServerPanel.setBounds(new Rectangle(14, 5, 700, 200));

        //
        // Accounts section.
        // Pass the controller to account panel
        //
        searchAccountPanel = new SearchAccountPanel(accountController);
        searchAccountPanel.setBounds(new Rectangle(14, 205, 700, 380));

        setLayout(null);
        add(searchMailServerPanel);
        add(searchAccountPanel);
    }

    /**
     * Set preferredSize of the panel
     *
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 650);
    }

    /**
     *
     */
    public void updateForm() {

        EmailConnectorConfig emailConf = (EmailConnectorConfig) getConfiguration();


    }

    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
    }

    // ----------------------------------------------------------- Private methods

    /**
     * Checks if the values provided by the user are all valid. In caso of errors,
     * a IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>anyvalue is not in the form <host>:<port>
     *                                  </ul>
     */
    private void validateValues() throws IllegalArgumentException {
        String value;
    }

    /**
     * Set properties with the values provided by the user.
     */
    private void getValues() {
        EmailConnectorConfig emailConf = (EmailConnectorConfig) getConfiguration();
    }

    private void initWSDao() throws Exception {

        String endPoint = "";
        String user = "";
        String pwd = "";
        int reloadingTime = 0;

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
    }

}
