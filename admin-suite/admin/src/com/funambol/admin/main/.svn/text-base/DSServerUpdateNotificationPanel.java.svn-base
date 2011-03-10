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

package com.funambol.admin.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.HREFJLabel;
import com.funambol.admin.util.Bundle;

/**
 * Panel used to notify a new DS Server version
 * @version $Id: DSServerUpdateNotificationPanel.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class DSServerUpdateNotificationPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    // First row
    private JLabel     jLabel1;

    // Firt part of the second row
    private JLabel     jLabel2;

    // The label with the url
    private HREFJLabel jLabel3;

    // The point after the url
    private JLabel     jLabel4;

    // The checkbox about "Don't show again this message...."
    private JCheckBox  jCheckBox1;

    // The ds server version to notify
    private String versionToNotify = "x.y.z"; // dummy version number

    // Second row label widths. Needed to concatenate the labels without issue
    // about font size
    private int label2Width = 0;
    private int label3Width = 0;
    private int label4Width = 0;

    // The preferred width of the panel
    private int panelWidth = 0;

    // ------------------------------------------------------------- Constructor
    /**
     * Creates new DSServerUpdateNotificationPanel
     */
    public DSServerUpdateNotificationPanel() {
        initComponents();
    }

    /**
     * Return the preferred size of this panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(panelWidth, 95);
    }

    /**
     * Sets the ds server version to notify
     */
    public void setVersionToNotify(String version) {
        versionToNotify = version;
        jLabel1.setText(
            MessageFormat.format(Bundle.getMessage(Bundle.NEW_DS_SERVER_VERSION_NOTIFICATION_1), new String[] {version})
        );
    }

    /**
     * Returns the versionToNotify
     * @return the version to notify (or already notified)
     */
    public String getVersionToNotify() {
        return this.versionToNotify;
    }

    /**
     * Is the checkbox "don't show again this message..." selected ?
     * @return true if the checkbox "don't show again this message..." is selected
     *         false otherwise
     */
    public boolean isDontShowAgainTheMessageSelected() {
        return jCheckBox1.isSelected();
    }

    /**
     * Deselects the checkbox "don't show again this message..."
     */
    public void deselectDontShowAgainTheMessage() {
        jCheckBox1.setSelected(false);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Inits components
     */
    private void initComponents() {
        jLabel1    = new JLabel();
        jLabel2    = new JLabel();
        jLabel3    = new HREFJLabel();
        jLabel4    = new JLabel();
        jCheckBox1 = new JCheckBox();

        setLayout(null);

        jLabel1.setBounds(20, 10, 455, 20);
        String label2Text = Bundle.getMessage(Bundle.NEW_DS_SERVER_VERSION_NOTIFICATION_2) + " ";
        Font label2Font = jLabel2.getFont();
        label2Width = jLabel2.getFontMetrics(label2Font).stringWidth(label2Text);

        jLabel2.setText(label2Text);
        jLabel2.setBounds(20, 35, label2Width, 20);

        String label3Text = Bundle.getMessage(Bundle.NEW_DS_SERVER_VERSION_NOTIFICATION_URL);
        Font label3Font = jLabel3.getFont();
        label3Width = jLabel3.getFontMetrics(label3Font).stringWidth(label3Text);
        jLabel3.setText(label3Text);
        jLabel3.setUrl(Bundle.getMessage(Bundle.NEW_DS_SERVER_VERSION_NOTIFICATION_URL));
        jLabel3.setBounds(20 + label2Width, 35, label3Width, 20);
        jLabel3.setForeground(Color.blue);

        String label4Text = ".";
        Font label4Font = jLabel4.getFont();
        label4Width = jLabel4.getFontMetrics(label4Font).stringWidth(label4Text);
        jLabel4.setText(label4Text);
        jLabel4.setBounds(20 + label2Width + label3Width, 35, label4Width, 20);

        jCheckBox1.setText(Bundle.getMessage(Bundle.SUPPRESS_UPDATES_NOTIFICATION));
        jCheckBox1.setBounds(18, 75, 560, 17);

        panelWidth = 20 + label2Width + label3Width + label4Width + 20;

        add(jLabel1);
        add(jLabel2);
        add(jLabel3);
        add(jLabel4);
        add(jCheckBox1);

        setFont(GuiFactory.loginPanelFont);
    }
}
