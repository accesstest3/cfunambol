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

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.core.CTCap;
import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.Property;

/**
 * Table model for the table with the ctcaps of a datastore
 *
 * @version $Id: CTCapTableModel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class CTCapTableModel extends AbstractTableModel {
    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(
                                                     Bundle.COL_TYPE),
                                             Bundle.getMessage(
                                                     Bundle.COL_VERSION),
                                             Bundle.getMessage(
                                                     Bundle.COL_FIELD_LEVEL),
                                             Bundle.getMessage(
                                                     Bundle.COL_PROPERTIES)};

    // ------------------------------------------------------------ Private data
    /**
     * the list of ctCaps to be displayed in the table
     */
    private ArrayList ctCaps = null;
    private JButton button = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a table model for the table with the ctcaps of a datastore
     *
     * @param dataStore the dataStore for wich the ctCaps will be displayed
     * @param button    reference to the button that will bring up the dialog
     *                  for ctCaps properties
     */
    public CTCapTableModel(DataStore dataStore, JButton button) {
        this.ctCaps = dataStore.getCTCaps();
        this.button = button;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return ctCaps.size();
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
            case 1:
            case 2:
                return false;
            case 3:
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
        if (c == 3)
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
    public Object getValueAt(int row, int col) {
        CTCap ctCap = (CTCap) ctCaps.get(row);
        switch (col) {
            case 0:
                return ctCap.getCTInfo().getCTType();
            case 1:
                String version=ctCap.getCTInfo().getVerCT();
                if(version==null) {
                    return "-";
                } else {
                    return version;
                }                
            case 2:
                return new Boolean(ctCap.isFieldLevel());
            case 3:
                return button;
            default:
                return "";
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
        CTCap ctCap = (CTCap) ctCaps.get(row);
        switch (col) {
            case 0:
                String val = (String) value;
                if ((val != null) && (!val.equals(""))) {
                    ctCap.getCTInfo().setCTType(val);
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_TYPE_EMPTY));
                }
                break;
            case 1:
                val = (String) value;
                if ((val != null) && (!val.equals(""))) {
                    ctCap.getCTInfo().setVerCT(val);
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_VERSION_EMPTY));
                    this.setValueAt("-",row,col);                    
                }
                break;
            case 2:
                val = (String) value;
                ctCap.setFieldLevel(new Boolean(val));
                break;
        }
    }

    /**
     * Adds a ctcap to the selected datastore
     *
     * @return the position in table where the new ctcap was added
     */
    public int addCTCap() {
        Property props[] = new Property[1];
        Property prop = new Property();
        prop.setDisplayName(Bundle.getMessage(Bundle.NEW_PROP));
        prop.setPropName(Bundle.getMessage(Bundle.NEW_PROP));
        props[0] = prop;
        CTCap newCTCap = new CTCap(new CTInfo(
                        Bundle.getMessage(Bundle.NEW_TYPE),
                        Bundle.getMessage(Bundle.NEW_VERSION)), false, props);               
        ctCaps.add(newCTCap);

        fireTableDataChanged();
        return ctCaps.indexOf(newCTCap);
    }

    /**
     * Removes the selected ctcap
     *
     * @param row the row in the table of the ctcap to be deleted
     */
    public void remCTCap(int row) {
        if (row >= 0) {
            if (getRowCount() == 1) {
                JOptionPane.showMessageDialog(
                        null, Bundle.getMessage(
                                Bundle.MSG_CTCAP_DELETE_ALL));
            } else {
                int answer = JOptionPane.showConfirmDialog(
                        null, Bundle.getMessage(
                                Bundle.MSG_DELETE_CTCAP),
                        Bundle.getMessage(
                                Bundle.MSG_DELETE_CTCAP_TITLE),
                        JOptionPane.YES_NO_OPTION);

                if (answer == 0) {
                    ctCaps.remove(row);
                }
            }
        }
        fireTableDataChanged();
    }
}
