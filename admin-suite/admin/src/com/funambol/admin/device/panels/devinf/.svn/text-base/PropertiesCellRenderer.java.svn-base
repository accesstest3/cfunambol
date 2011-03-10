/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.admin.device.panels.devinf;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.funambol.admin.ui.GuiFactory;

/**
 * Cell renderer for the table with filterCaps of a datastore. It is used to
 * make the last column of the table to display and work as a button
 *
 * @version $Id: PropertiesCellRenderer.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class PropertiesCellRenderer
extends JPanel
implements TableCellRenderer {

    //-------------------------------------------------------------- Constructor
    /**
     * Creates a JPanel with a button that brings up a dialog. This JPanel is
     * the component for drawing a cell in a table
     */
    public PropertiesCellRenderer() {
        this.setBackground(Color.WHITE);
        //this.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
        this.setLayout(new GridLayout(1, 1));

        JButton but = new JButton(". . .");
        but.setFont(GuiFactory.defaultFont);
        this.add(but);
    }

    //----------------------------------------------------------- Public methods
    /**
     * Returns the component used for drawing the cell. This method is used to
     * configure the renderer appropriately before drawing.
     *
     * @param table      the JTable that is asking the renderer to draw
     * @param object     the value of the cell to be rendered
     * @param isSelected true if the cell is to be rendered with the selection
     *                   highlighted; otherwise false
     * @param hasFocus   if true, render cell appropriately. For example, put a
     *                   special border on the cell, if the cell can be edited,
     *                   render in the color used to indicate editing
     * @param row        the row index of the cell being drawn. When drawing the
     *                   header, the value of row is -1
     * @param column     the column index of the cell being drawn
     *
     * @return the component used for drawing the cell
     */
    public Component getTableCellRendererComponent(JTable table,
                                                   Object object,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        return this;
    }
}
