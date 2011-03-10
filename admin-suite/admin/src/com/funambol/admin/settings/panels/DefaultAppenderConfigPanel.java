/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.admin.settings.panels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.io.Serializable;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Panel shown when an Appender does not have a specified configuration panel.
 *
 * @version $Id$
 */
public class DefaultAppenderConfigPanel
extends ManagementObjectPanel
implements Serializable {

    // ------------------------------------------------------------ Private data
    /** label for the panel's name */
    private JLabel panelNameLabel = new JLabel();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    private JLabel messageLabel = new JLabel();

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel to edit a console appender.
     */
    public DefaultAppenderConfigPanel() {

        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 245);
    }

    public void updateForm() {
        // do nothing
    }

    // --------------------------------------------------------- Private methods
    /**
     * Creates the panel
     */
    private void init() {

        this.setLayout(null);

        titledBorder1 = new TitledBorder("");

        panelNameLabel.setFont(GuiFactory.titlePanelFont);
        panelNameLabel.setText(Bundle.getMessage(Bundle.LABEL_DEFAULT_APPENDER_SETTINGS));
        panelNameLabel.setBounds(new Rectangle(14, 5, 245, 28));
        panelNameLabel.setAlignmentX(SwingConstants.CENTER);
        panelNameLabel.setBorder(titledBorder1);

        messageLabel.setFont(GuiFactory.defaultFont);
        messageLabel.setBounds(new Rectangle(14, 53, 500, 48));

        messageLabel.setText(Bundle.getMessage(Bundle.MSG_APPENDER_NOT_CONFIGURABLE));

        this.add(panelNameLabel, null);
        this.add(messageLabel  , null);
    }
}
