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

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DSMem;

/**
 * Table model for the table with the DSMem properties of a datastore
 *
 * @version $Id: DSMemTableModel.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class DSMemTableModel extends AbstractTableModel {
    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(
                                                     Bundle.COL_PEOPERTY),
                                             Bundle.getMessage(
                                                     Bundle.COL_VALUE)};
    private final static String[] DSMEM_PROPERTIES = {"SharedMem", "MaxMem", "MaxId"};

    // ------------------------------------------------------------ Private data
    private DSMem dsMem = null;
    private DataStore dataStore = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates table model for the table with the DSMem properties of a
     * datastore
     *
     * @param dataStore the dataStore for wich the DSMem properties will be
     *                  displayed
     */
    public DSMemTableModel(DataStore dataStore) {
        this.dataStore = dataStore;
        this.dsMem = dataStore.getDSMem();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return DSMEM_PROPERTIES.length;
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
            return DSMEM_PROPERTIES[row];
        } else if (col == 1) {
            //properties value
            if (dsMem == null) {
                return "";
            } else {
                switch (row) {
                    case 0:
                        return new Boolean(dsMem.isSharedMem());
                    case 1:
                        return new Long(dsMem.getMaxMem());
                    case 2:
                        return new Long(dsMem.getMaxID());
                    default:
                        return "";
                }
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
            if (dsMem == null) {
                DSMem newDsMem = new DSMem();
                dataStore.setDSMem(newDsMem);
                dsMem = dataStore.getDSMem();
            }
            switch (row) {
                case 0: //shared mem
                    dsMem.setSharedMem(new Boolean((String) value));
                    break;
                case 1: //max mem
                    if ((value == null) || (((String) value).equals(""))) {
                        dsMem.setMaxMem(0L);
                    } else {
                        try {
                            dsMem.setMaxMem(
                                    (new Long((String) value)).longValue());
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(
                                    null, Bundle.getMessage(
                                            Bundle.MSG_INVALID_MAX_MEM));
                        }
                    }
                    break;
                case 2: //Max id
                    if ((value == null) || (((String) value).equals(""))) {
                        dsMem.setMaxID(0L);
                    } else {
                        try {
                            dsMem.setMaxID(
                                    (new Long((String) value)).longValue());
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(
                                    null, Bundle.getMessage(
                                            Bundle.MSG_INVALID_MAX_ID));
                        }
                    }
                    break;
            }
        }
    }
}
