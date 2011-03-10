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
package com.funambol.admin.login;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;

/**
 * Login panel shown during login process.
 *
 *
 * @version $Id: LoginPanel.java,v 1.6 2007-11-28 10:28:19 nichele Exp $
 */
public class LoginPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    private JTextField     hostEntry;
    private JTextField     proxyHostEntry;
    private JLabel         hostLabel;
    private JLabel         proxyHostLabel;
    private JLabel         userLabel;
    private JTextField     userEntry;
    private JTextField     proxyUserEntry;
    private JLabel         proxyUserLabel;
    private JPasswordField passwordEntry;
    private JPasswordField proxyPasswordEntry;
    private JLabel         passwordLabel;
    private JLabel         proxyPasswordLabel;
    private JTextField     portEntry;
    private JTextField     proxyPortEntry;
    private JLabel         portLabel;
    private JLabel         proxyPortLabel;
    private JPanel         proxyPanel;
    private JCheckBox      proxyCheck;
    private JCheckBox      rememberCheck;

    private AdminConfig config = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Create a new LoginPanel
     */
    public LoginPanel() {
        init();
    }

    /**
     * Init component's panel.
     */
    void init() {
        setLayout(null);

        setFont(GuiFactory.loginPanelFont);
        hostLabel          = new JLabel();
        hostEntry          = new JTextField();
        userLabel          = new JLabel();
        userEntry          = new JTextField();
        passwordLabel      = new JLabel();
        passwordEntry      = new JPasswordField();
        portLabel          = new JLabel();
        portEntry          = new JTextField();
        rememberCheck      = new JCheckBox();
        proxyCheck         = new JCheckBox();
        proxyPanel         = new JPanel();
        proxyHostLabel     = new JLabel();
        proxyHostEntry     = new JTextField();
        proxyPortEntry     = new JTextField();
        proxyUserLabel     = new JLabel();
        proxyUserEntry     = new JTextField();
        proxyPasswordLabel = new JLabel();
        proxyPasswordEntry = new JPasswordField();
        proxyPortLabel     = new JLabel();

        setLayout(null);

        int x1  = 20;
        int dx1 = 100;
        int x2  = 100;
        int dx2 = 120;
        int y   = 20;
        int dy  = 30;

        hostLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_HOST_NAME) + ':');
        add(hostLabel);
        hostLabel.setBounds(x1, y, dx1, 20);

        add(hostEntry);
        hostEntry.setBounds(x2, y, dx2, 20);

        portLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_PORT) + ':');
        add(portLabel);
        portLabel.setBounds(250, y, 31, 20);

        add(portEntry);
        portEntry.setBounds(290, y, 50, 20);

        y += dy;

        userLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_USER) + ':');
        add(userLabel);
        userLabel.setBounds(x1, y, dx1, 20);

        add(userEntry);
        userEntry.setBounds(x2, y, dx2, 20);

        y += dy;

        passwordLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_PWD) + ':');
        add(passwordLabel);
        passwordLabel.setBounds(x1, y, dx1, 20);

        add(passwordEntry);
        passwordEntry.setBounds(x2, y, dx2, 20);

        y += dy;

        rememberCheck.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_REMEMBER_PASSWORD));
        add(rememberCheck);
        rememberCheck.setBounds(200, y, 150, 23);

        proxyCheck.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_USE_PROXY));
        add(proxyCheck);
        proxyCheck.setBounds(17, y, 98, 23);

        proxyCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setProxyEntriesState(proxyCheck.isSelected());
            }
        });

        proxyPanel.setLayout(null);

        proxyPanel.setBorder(new TitledBorder(Bundle.getMessage(Bundle.LOGIN_PANEL_PROXY_SETTINGS)));
        proxyHostLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_HOST_NAME) + ':');
        proxyPanel.add(proxyHostLabel);
        proxyHostLabel.setBounds(15, 40, 100, 15);

        proxyPanel.add(proxyHostEntry);
        proxyHostEntry.setBounds(100, 40, 120, 20);

        proxyPanel.add(proxyPortEntry);
        proxyPortEntry.setBounds(290, 40, 50, 20);

        proxyUserLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_USER) + ':');
        proxyPanel.add(proxyUserLabel);
        proxyUserLabel.setBounds(20, 70, 100, 20);

        proxyPanel.add(proxyUserEntry);
        proxyUserEntry.setBounds(100, 70, 120, 20);

        proxyPasswordLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_PWD) + ':');
        proxyPanel.add(proxyPasswordLabel);
        proxyPasswordLabel.setBounds(20, 100, 100, 20);

        proxyPanel.add(proxyPasswordEntry);
        proxyPasswordEntry.setBounds(100, 100, 120, 20);

        proxyPortLabel.setText(Bundle.getMessage(Bundle.LOGIN_PANEL_PORT) + ':');
        proxyPanel.add(proxyPortLabel);
        proxyPortLabel.setBounds(250, 40, 31, 20);

        add(proxyPanel);
        proxyPanel.setBounds(10, y + 40, 350, 130);

    }

    /**
     * Load values
     * @param config value to load
     */
    public void loadValues() {

        config = AdminConfig.getAdminConfig();
        
        portEntry.setText(config.getServerPort());
        hostEntry.setText(config.getHostName());
        userEntry.setText(config.getUser());

        if (config.getRememberPassword()) {
            passwordEntry.setText(config.getPassword());
        } else {
            passwordEntry.setText("");
            passwordEntry.setCaretPosition(0);
        }
        rememberCheck.setSelected(config.getRememberPassword());

        proxyCheck.setSelected(config.isUseProxy());
        proxyHostEntry.setText(config.getProxyHostName());
        proxyPortEntry.setText(config.getProxyPort());
        proxyUserEntry.setText(config.getProxyUser());
        proxyPasswordEntry.setText(config.getProxyPassword());

        this.setProxyEntriesState(config.isUseProxy());
    }

    /**
     * Returns sycnServerConfiguration.
     * @return <code>HostConfiguration</code> with the values
     */
    public void saveOptions() {
        config.setUser(userEntry.getText().trim());
        config.setPassword(new String(passwordEntry.getPassword()).trim());
        config.setHostName(hostEntry.getText().trim());
        config.setServerPort(portEntry.getText().trim());
        config.setRememberPassword(rememberCheck.isSelected());
        config.setProxyUser(proxyUserEntry.getText().trim());
        config.setProxyPassword(new String(proxyPasswordEntry.getPassword()).trim());
        config.setProxyHostName(proxyHostEntry.getText().trim());
        config.setProxyPort(proxyPortEntry.getText().trim());
        config.setUseProxy(proxyCheck.isSelected());

        config.saveConfiguration();
    }

    /**
     * Validates the panel input. If an incorrect parameter is found, an
     * IllegalArguementException is thrown with a message explaining the error.
     *
     * @throws IllegalAgumentException in case of wrong input values
     */
    public void validateInput()
    throws IllegalArgumentException {
        String value;

        value = portEntry.getText().trim();
        try {
            int i = Integer.parseInt(value);
            if (i<0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            String msg = MessageFormat.format(
                             Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                             new String[] {
                                 Bundle.getMessage(Bundle.LOGIN_PANEL_PORT),
                                 "0",
                                 String.valueOf(Short.MAX_VALUE)
                             }
                         );
            throw new IllegalArgumentException(msg);
        }

        if (proxyCheck.isSelected()) {
            value = proxyHostEntry.getText().trim();
            if (value.length() == 0) {
                String msg = MessageFormat.format(
                                 Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                                 new String[] {
                                     Bundle.getMessage(Bundle.LOGIN_PANEL_PROXY_HOST)
                                 }
                             );
                throw new IllegalArgumentException(msg);
            }
            value = proxyPortEntry.getText().trim();
            try {
                int i = Integer.parseInt(value);
                if (i<0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                String msg = MessageFormat.format(
                                 Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                                 new String[] {
                                     Bundle.getMessage(Bundle.LOGIN_PANEL_PROXY_PORT),
                                     "0",
                                     String.valueOf(Short.MAX_VALUE)
                                 }
                             );
                throw new IllegalArgumentException(msg);
            }
        }

    }

    /**
     * Return the preferred size of this panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(370, 350);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Enables/disables the proxy entry fields accordingly to <i>enable</i>
     *
     * @param enable true if the proxy entries must be enabled, false if they
     *        must be disabled.
     */
    private void setProxyEntriesState(boolean enable) {
        proxyHostEntry.setEnabled(enable);
        proxyPortEntry.setEnabled(enable);
        proxyUserEntry.setEnabled(enable);
        proxyPasswordEntry.setEnabled(enable);
    }
}
