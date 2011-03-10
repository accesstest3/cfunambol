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

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.VerDTD;

/**
 * Table model for the table with properties of capabilities
 *
 * @version $Id: CapabilitiesPropsTableModel.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class CapabilitiesPropsTableModel extends AbstractTableModel {

    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(Bundle.COL_PEOPERTY),
                                             Bundle.getMessage(Bundle.COL_VALUE)};
    private final static String[] CAPS_PROPERTIES = {"Version", "Man", "Mod", "FwV", "SwV", "HwV", "UTC", "LO", "NOC"};

    // ------------------------------------------------------------ Private data
    /**
     * the object containing device capabilities that will be displayed in
     * table
     */
    private DevInf devInf = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new table model used for the table with properties of
     * capabilities
     *
     * @param devInf the object containing device capabilities that will be
     *               displayed in table
     */
    public CapabilitiesPropsTableModel(DevInf devInf) {
        this.devInf = devInf;
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return CAPS_PROPERTIES.length;
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
     * Returns the value for the cell at col and row.
     *
     * @param row the row whose value is to be queried
     * @param col the column whose value is to be queried
     *
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            //properties name
            return CAPS_PROPERTIES[row];
        } else if (col == 1 && this.devInf != null) {
            //properties value
            switch (row) {
                case 0:
                    return this.devInf.getVerDTD().getValue();
                case 1:
                    return this.devInf.getMan();
                case 2:
                    return devInf.getMod();
                case 3:
                    return devInf.getFwV();
                case 4:
                    return devInf.getSwV();
                case 5:
                    return devInf.getHwV();
                case 6:
                    return new Boolean(devInf.isUTC());
                case 7:
                    return new Boolean(devInf.isSupportLargeObjs());
                case 8:
                    return new Boolean(devInf.isSupportNumberOfChanges());
                default:
                    return "";
            }
        }
        return "";
    }

    /**
     * Sets the value in the cell at col and row to value.
     *
     * @param value the new value
     * @param row   the row whose value is to be changed
     * @param col   the column whose value is to be changed
     */
    public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            switch (row) {
                case 0: //versionDTD
                    if ((value != null) && (!((String) value).equals(""))) {
                        devInf.getVerDTD().setValue((String) value);
                    } else {
                        JOptionPane.showMessageDialog(null, Bundle.getMessage(Bundle.MSG_VERSION_EMPTY));
                    }
                    break;
                case 1: //man
                    devInf.setMan((String) value);
                    break;
                case 2: //Mod
                    devInf.setMod((String) value);
                    break;
                case 3: //FwV
                    devInf.setFwV((String) value);
                    break;
                case 4: //SwV
                    devInf.setSwV((String) value);
                    break;
                case 5: //HwV
                    devInf.setHwV((String) value);
                    break;
                case 6: //UTC
                    devInf.setUTC(new Boolean((String) value));
                    break;
                case 7: //LO
                    devInf.setSupportLargeObjs(new Boolean((String) value));
                    break;
                case 8: //NOC
                    devInf.setSupportNumberOfChanges(new Boolean((String) value));
                    break;
            }
        }
    }
}
