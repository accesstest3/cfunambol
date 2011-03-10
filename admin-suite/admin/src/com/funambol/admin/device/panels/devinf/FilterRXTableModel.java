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

import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;

/**
 * Table model for the table with the informations about a datastore's
 * filterRxs
 *
 * @version $Id: FilterRXTableModel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class FilterRXTableModel extends AbstractTableModel {

    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(
                                                     Bundle.COL_TYPE),
                                             Bundle.getMessage(
                                                     Bundle.COL_VERSION)};

    // ------------------------------------------------------------ Private data
    /**
     * list with the filterRxs that will be displayed by the table
     */
    private ArrayList filterRxs = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a  table model for the table with the informations about a
     * datastore's filterRxs
     *
     * @param dataStore the dataStrore for wich the filterRXs are displayed
     */
    public FilterRXTableModel(DataStore dataStore) {
        this.filterRxs = dataStore.getFilterRxs();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return filterRxs.size();
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
        CTInfo cti = (CTInfo) filterRxs.get(row);
        switch (col) {
            case 0:
                return cti.getCTType();
            case 1:
                String version=cti.getVerCT();
                if(version==null) {
                    return "-";
                } else {
                    return version;
                }
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
        CTInfo cti = (CTInfo) filterRxs.get(row);
        String val = (String) value;
        switch (col) {
            case 0:
                if ((val != null) && (!val.equals(""))) {
                    //test if the combination new type and version
                    // already exists in the table
                    boolean exists = false;
                    int i = 0;
                    CTInfo temp = null;
                    while (i < filterRxs.size()) {
                        if(i==row) {
                            i++;
                            continue;
                        }
                        temp = (CTInfo) filterRxs.get(i);
                        if ((temp.getCTType().equals(val)) && (temp.getVerCT().equals(
                                cti.getVerCT()))) {
                            exists = true;
                        }
                        i++;
                    }
                    if (!exists) {
                        cti.setCTType(val);
                    } else {
                        JOptionPane.showMessageDialog(
                                null, Bundle.getMessage(
                                        Bundle.MSG_FILTER_RX_ALREADY_EXISTS)
                                + val
                                + Bundle.getMessage(
                                        Bundle.MSG_TYPE_VERSION_ALREADY_EXISTS_1)
                                + cti.getVerCT()
                                + Bundle.getMessage(
                                        Bundle.MSG_TYPE_VERSION_ALREADY_EXISTS_2));
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_TYPE_EMPTY));
                }
                break;
            case 1:
                if ((val != null) && (!val.equals(""))) {
                    //test if the combination type and new version
                    // already exists in the table
                    boolean exists = false;
                    int i = 0;
                    CTInfo temp = null;
                    while (i < filterRxs.size()) {
                        if(i==row) {
                            i++;
                            continue;
                        }
                        temp = (CTInfo) filterRxs.get(i);
                        if ((temp.getCTType().equals(cti.getCTType()))
                                && (temp.getVerCT().equals(val))) {
                            exists = true;
                        }
                        i++;
                    }
                    if (!exists) {
                        cti.setVerCT(val);
                    } else {
                        JOptionPane.showMessageDialog(
                                null, Bundle.getMessage(
                                        Bundle.MSG_FILTER_RX_ALREADY_EXISTS)
                                + cti.getCTType()
                                + Bundle.getMessage(
                                        Bundle.MSG_TYPE_VERSION_ALREADY_EXISTS_1)
                                + val
                                + Bundle.getMessage(
                                        Bundle.MSG_TYPE_VERSION_ALREADY_EXISTS_2));
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, Bundle.getMessage(
                                    Bundle.MSG_VERSION_EMPTY));
                    this.setValueAt("-",row,col);
                }
                break;
        }
    }

    /**
     * Adds a filterRx to the selected datastore
     *
     * @return the position in table where the new filterRx was added
     */
    public int addFilterRX() {
        //find the first "New Type i" that does not exists in the table
        boolean exists = true;
        String name = Bundle.getMessage(Bundle.NEW_TYPE);
        String typeName = name;
        CTInfo temp = null;
        int nr = 1;
        while (exists) {
            typeName = name + " " + nr;
            exists = false;
            for (int i = 0; i < filterRxs.size(); i++) {
                temp = (CTInfo) filterRxs.get(i);
                if (temp.getCTType().equals(typeName)) {
                    exists = true;
                }
            }
            nr++;
        }

        //add the new CTI to the filterRx list
        CTInfo newCTI = new CTInfo(
                typeName, Bundle.getMessage(Bundle.NEW_VERSION));
        filterRxs.add(newCTI);

        fireTableDataChanged();
        return filterRxs.indexOf(newCTI);
    }

    /**
     * Removes the selected filterRx
     *
     * @param row the row in the table of the filterRx to be deleted
     */
    public void remFilterRX(int row) {
        if ((row >= 0) && (filterRxs.size() > 0)) {
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(
                            Bundle.MSG_DELETE_FILTERRX),
                    Bundle.getMessage(
                            Bundle.MSG_DELETE_FILTERRX_TITLE),
                    JOptionPane.YES_NO_OPTION);

            if (answer == 0) {
                filterRxs.remove(row);
            }
        }
        fireTableDataChanged();
    }

}
