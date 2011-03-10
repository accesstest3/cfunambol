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

package com.funambol.admin.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.funambol.admin.util.ClauseTools;

/**
 * Supplies common graphics object.
 *
 *
 * @version $Id: GuiFactory.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 */
public class GuiFactory {

    // ------------------------------------------------------------- Public data
    public final static Font defaultFont = new Font("Arial", 0, 10);
    public final static Font defaultTableFont = new Font("Arial", 0, 10);
    public final static Font defaultTableHeaderFont = new Font("Arial", 1, 11);
    public final static Font titlePanelFont = new java.awt.Font("Arial", 1, 14);
    public final static Font loginPanelFont = new java.awt.Font("Arial", 0, 11);

    // ------------------------------------------------------------ Private data
    private final static String[] comboSearchDefaultValue = ClauseTools.getAvailableOperators();

    // ---------------------------------------------------------- Public methods

    /**
     * Returns default combo search with the default criteria.
     *
     * @return default combo for search form
     */
    public static JComboBox getDefaultComboSearch() {
        JComboBox combo = new JComboBox();
        int num = comboSearchDefaultValue.length;
        for (int i = 0; i < num; i++) {
            combo.addItem(comboSearchDefaultValue[i]);
        }
        return combo;
    }

    /**
     * Sets the font of each JEdit/JLable/JButton component inside a JPanel to
     * <i>defaultFont</i>
     *
     * @param panel the JPanel
     *
     */
    public static void setDefaultFont(JPanel panel) {
        Component[] components = panel.getComponents();

        for (int i=0; (components != null) && (i<components.length); ++i) {
            if ((components[i] instanceof JLabel) ||
                (components[i] instanceof JTextComponent) ||
                (components[i] instanceof JCheckBox) ||
                (components[i] instanceof JButton)) {
                components[i].setFont(defaultFont);
            }
        }
    }

}
