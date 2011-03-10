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

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

import com.funambol.framework.core.CTCap;

/**
 * Cell Editor and Tabel cell editor implementations for ctCap properties table
 *
 * @version $Id: CTCapPropertiesTableEditor.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class CTCapPropertiesTableEditor 
extends AbstractCellEditor
implements TableCellEditor, ActionListener {

    // --------------------------------------------------------------- Constants
    protected static final String ACTION_EDIT = "ACTION_EDIT_CTCAP";
    // ------------------------------------------------------------ Private data
    /**
     * the button that bring up the dialog for ctCap properties
     */
    private JButton button;
    /**
     * reference to the ctCap properties dialog
     */
    private CTCapPropertiesDialog dialog;
    /**
     * currently selected row in ctCap table
     */
    private Integer currentRow;
    /**
     * the list with the ctCaps displayed in the table
     */
    private ArrayList ctCaps;

    // ------------------------------------------------------------- Constructor

    /**
     * Constructor for the table editor. Sets up the button that will bring out
     * the ctCap properties editing dialog.
     *
     * @param ctCaps CTCaps of the devinf
     */
    public CTCapPropertiesTableEditor(ArrayList ctCaps) {
        this.ctCaps = ctCaps;
        button = new JButton();
        button.setActionCommand(ACTION_EDIT);
        button.addActionListener(this);
        dialog = CTCapPropertiesDialog.createDialog(button, true);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if (ACTION_EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            dialog.setCTCap((CTCap) ctCaps.get(currentRow.intValue()));
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();
        }
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editors
     */
    public Object getCellEditorValue() {
        return currentRow;
    }

    /**
     * Sets an initial value for the editor. This will cause the editor to
     * stopEditing and lose any partially edited value if the editor is editing
     * when this method is called. Returns the component that should be added to
     * the client's Component hierarchy. Once installed in the client's
     * hierarchy this component will then be able to draw and receive user
     * input.
     *
     * @param table      the JTable that is asking the editor to edit This
     *                   parameter can be null.
     * @param value      the value of the cell to be edited. It is up to the
     *                   specific editor to interpret and draw the value. eg. if
     *                   value is the String "true", it could be rendered as a
     *                   string or it could be rendered as a check box that is
     *                   checked. null is a valid value.
     * @param isSelected true is the cell is to be renderer with selection
     *                   highlighting
     * @param row        the row of the cell being edited
     * @param column     the column of the cell being edited
     *
     * @return the component for editing
     */
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentRow = new Integer(row);
        return button;
    }
}
