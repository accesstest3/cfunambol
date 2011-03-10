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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Create a panel with buttons to edit, cancel and add users.
 *
 *
 * @version $Id: ButtonSearchUserPanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 **/
public class ButtonSearchUserPanel extends JPanel {
    // ------------------------------------------------------------ Private data

    /**  button for cancel action */
    private JButton jBCancel = new JButton();

    /**  button for edit action */
    private JButton jBEdit = new JButton();

    /**  button for add action */
    private JButton jBAdd = new JButton();

    // ------------------------------------------------------------ Constructors
    /**
     * Generate the panel
     */
    public ButtonSearchUserPanel() {
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
        return new Dimension(200, 230);
    }

    /**
     * Add the listener recived as parameter to the buttons of the panel
     * @param actionListener the listener of the action
     */
    public void addButtonActionListener(ActionListener actionListener) {
        jBCancel.addActionListener(actionListener);
        jBEdit.addActionListener(actionListener);
        jBAdd.addActionListener(actionListener);
    }

    /**
     * Enable or disable buttons
     * @param enabled true to enable the button, otherwise false
     */
    public void enableButtons(boolean enabled) {
        jBCancel.setEnabled(enabled);
        jBEdit.setEnabled(enabled);
    }

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void jbInit() throws Exception {
        this.setLayout(null);

        jBAdd.setBounds(new Rectangle(55, 75, 79, 27));
        jBAdd.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD));
        jBAdd.setActionCommand(Constants.ACTION_COMMAND_ADD_USER);

        jBCancel.setBounds(new Rectangle(55, 125, 79, 27));
        jBCancel.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_DELETE));
        jBCancel.setActionCommand(Constants.ACTION_COMMAND_DELETE_USER);

        jBEdit.setBounds(new Rectangle(55, 175, 79, 27));
        jBEdit.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_EDIT));
        jBEdit.setActionCommand(Constants.ACTION_COMMAND_EDIT_USER);

        jBCancel.setFont(GuiFactory.defaultFont);
        jBEdit.setFont(GuiFactory.defaultFont);
        jBAdd.setFont(GuiFactory.defaultFont);

        this.add(jBAdd, null);
        this.add(jBCancel, null);
        this.add(jBEdit, null);        

        enableButtons(false);
    }

}
