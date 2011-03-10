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

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.core.Ext;

/**
 * Table model for the table with the Ext of the capabilties
 *
 * @version $Id: ExtTableModel.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class ExtTableModel extends AbstractTableModel {

    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(
                                                 Bundle.COL_XNAME),
                                             Bundle.getMessage(
                                                 Bundle.COL_XVALUES)};

    // ------------------------------------------------------------ Private data
    private ArrayList exts = null;
    private JButton button = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a table model for the table with the Ext of the capabilties
     *
     * @param exts   the list with the exts that wll be displayed in the table
     * @param button reference to the button that will bring up the dialog for
     *               ext properties
     */
    public ExtTableModel(ArrayList exts, JButton button) {
        this.exts = exts;
        this.button = button;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return exts.size();
    }

    /**
     * Returns the name of the column at col
     *
     * @param col the index of the column
     *
     * @return the name of the column
     */
    public String getColumnName(int col) {
        return columns[col];
    }

    /**
     * Returns the number of columns in the model
     *
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns true if the cell at row and col is editable. Otherwise,
     * setValueAt on the cell will not change the value of that cell.
     *
     * @param row the row whose value to be queried
     * @param col the column whose value to be queried
     *
     * @return true if the cell is editable
     */
    public boolean isCellEditable(int row, int col) {
         switch (col) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                return false;
        }
    }

    /**
     * JTable uses this method to determine the default renderer/editor for each
     * cell.
     *
     * @param c the column for wich the class is needed
     *
     * @return the class of the objects in column c
     */
    public Class getColumnClass(int c) {
        if (c == 1)
            return getValueAt(0, c).getClass();
        else
            return String.class;

    }

    /**
     * Returns the value for the cell at col and row
     *
     * @param row the row whose value is to be queried
     * @param col the column whose value is to be queried
     *
     * @return the value Object at the specified cell
     */
    public Object getValueAt(final int row, int col) {

        if (col == 0) {
            Ext ext = (Ext) exts.get(row);
            return ext.getXNam();
        } else {
            return button;
        }
    }

    /**
     * Sets the value in the cell at col and row to value.
     *
     * @param value the new value
     * @param row   the row whose value is to be changed
     * @param col   the column whose value is to be changed
     */
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            String val = (String) value;
            if ((val != null) && (!val.equals(""))) {
                Ext ext = (Ext) exts.get(row);
                //check if the name does exist already
                boolean exists = false;
                Ext temp = null;
                for (int i = 0; i < exts.size(); i++) {
                    temp = (Ext) exts.get(i);
                    if ((temp.getXNam().equals(val)) && (i != row)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    ext.setXNam(val);
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_EXT_ALREADY_EXISTS));
                }
            } else {
                JOptionPane.showMessageDialog(
                        null, Bundle.getMessage(
                                Bundle.MSG_EXT_EMPTY));
            }
        }
    }

    /**
     * Add a new Extension to the table
     *
     * @return the position in table where the new EXT was added
     */
    public int addExt() {
        //add ext
        Ext newExt = new Ext();
        newExt.setXNam("");
        exts.add(newExt);
        //find a name like New Ext i that does not exists in the table nymore
        boolean exists = true;
        Ext temp = null;
        String name = Bundle.getMessage(Bundle.NEW_EXT);
        String xName = Bundle.getMessage(Bundle.NEW_EXT);
        int nr = 1;
        while (exists) {
            xName = name + " " + nr;
            exists = false;
            for (int i = 0; i < exts.size(); i++) {
                temp = (Ext) exts.get(i);
                if (temp.getXNam().equals(xName)) {
                    exists = true;
                }
            }
            nr++;
        }
        setValueAt(xName, exts.indexOf(newExt), 0);

        fireTableDataChanged();
        return exts.indexOf(newExt);
    }

    /**
     * Removes the Ext from specified row
     *
     * @param row the row of the EXT to be removed
     */
    public void remExt(int row) {
        if ((row >= 0) && (exts.size() > 0)) {
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(Bundle.MSG_DELETE_EXT),
                    Bundle.getMessage(Bundle.MSG_DELETE_EXT_TITLE),
                    JOptionPane.YES_NO_OPTION);

            if (answer == 0) {
                exts.remove(row);
                fireTableDataChanged();
            }
        }
    }
}
