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

import com.funambol.framework.core.PropParam;
import com.funambol.framework.core.Property;

/**
 * Table model for the table with the ctcap properties parameters
 *
 * @version $Id: CTCapPropParamTableModel.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class CTCapPropParamTableModel extends AbstractTableModel {
    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(
                                                    Bundle.COL_PARAM_NAME),
                                             Bundle.getMessage(
                                                     Bundle.COL_DISPLAY_NAME),
                                             Bundle.getMessage(
                                                     Bundle.COL_DATA_TYPE)};

    // ------------------------------------------------------------ Private data
    private ArrayList ctCapPropParams = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a table model for the table with the ctcap properties parameters
     *
     * @param prop the properties of a ctCap for wich the parameters will be
     *             displayed
     */
    public CTCapPropParamTableModel(Property prop) {
        this.ctCapPropParams = prop.getPropParams();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        if (ctCapPropParams != null)
            return ctCapPropParams.size();
        else
            return 0;
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
        return false;
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
        PropParam param = (PropParam) ctCapPropParams.get(row);
        switch (col) {
            case 0:
                return param.getParamName();
            case 1:
                return param.getDisplayName();
            case 2:
                return param.getDataType();
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
        PropParam param = (PropParam) ctCapPropParams.get(row);
        String val = (String) value;
        switch (col) {
            case 0:
                if ((val != null) && (!val.equals(""))) {
                    param.setParamName(val);
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_PARAM_NAME_EMPTY));
                }
                break;
            case 1:
                param.setDisplayName(val);
                break;
            case 2:
                param.setDataType(val);
                break;
        }
    }

    /**
     * Adds a ctcap property
     *
     * @return the position in table where the new ctcap property was added
     */
    public int addPropParam() {
        PropParam newParam = new PropParam();
        newParam.setParamName(
                Bundle.getMessage(Bundle.NEW_CTCAP_PROP_PARAM));

        ctCapPropParams.add(newParam);
        fireTableDataChanged();
        return ctCapPropParams.indexOf(newParam);
    }

    /**
     * Removes the selected ctcap  property
     *
     * @param row the row in the table of the ctcap property to be deleted
     */
    public void remPropParam(int row) {
        if (row >= 0) {
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(
                            Bundle.MSG_DELETE_CTCAPPARAM),
                    Bundle.getMessage(
                            Bundle.MSG_DELETE_CTCAPPARAM_TITLE),
                    JOptionPane.YES_NO_OPTION);

            if (answer == 0) {
                ctCapPropParams.remove(row);
            }
        }
        fireTableDataChanged();
    }
}
