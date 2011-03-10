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

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;

/**
 * Table model for the table with the informations about rxs of a datastore
 *
 * @version $Id: RXTableModel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class RXTableModel extends AbstractTableModel {
    // --------------------------------------------------------------- Constants
    private final static String[] columns = {
        Bundle.getMessage(Bundle.COL_TYPE),
        Bundle.getMessage(Bundle.COL_VERSION),
        Bundle.getMessage(Bundle.COL_PREFERRED)
    };

    // ------------------------------------------------------------ Private data
    /**
     * List with rx of the datastore and preferredRx
     */
    private ArrayList extendedRxs = null;

    /**
     * List with the rx of the datastore
     */
    private ArrayList rxs = null;

    /**
     * The datastores for which the rx are displayed in the table
     */
    private DataStore dataStore = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a table model for the table with the informations about rxs of a
     * datastore
     *
     * @param dataStore the datastore for wich the rx wil be displayed
     */
    public RXTableModel(DataStore dataStore) {
        this.rxs = dataStore.getRxs();
        extendedRxs = new ArrayList();
        if (dataStore.getRxPref() != null) {
            extendedRxs.add(dataStore.getRxPref());
        }
        extendedRxs.addAll(this.rxs);

        this.dataStore = dataStore;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return extendedRxs.size();
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
        CTInfo cti = (CTInfo) extendedRxs.get(row);
        switch (col) {
            case 0:
                return cti.getCTType();
            case 1:
                if(cti.getVerCT()==null) {
                    return "-";
                } else {
                    return cti.getVerCT();
                }
            case 2:
                if ((dataStore.getRxPref() != null) &&
                    (cti.getCTType().equals(dataStore.getRxPref().getCTType())) &&
                    (cti.getVerCT()!=null)&&
                    (cti.getVerCT().equals(dataStore.getRxPref().getVerCT())) ) {
                    return new Boolean(true);
                } else {
                    return new Boolean(false);
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

        CTInfo cti = (CTInfo) extendedRxs.get(row);
        String val = (String) value;
        switch (col) {
            case 0:
                if ((val != null) && (!val.equals(""))) {
                    //test if the combination new type and version already
                    // exists in the table
                    boolean exists = false;
                    int i = 0;
                    CTInfo temp = null;
                    while (i < extendedRxs.size()) {
                        if(i==row) {
                            i++;
                            continue;
                        }
                        temp = (CTInfo) extendedRxs.get(i);
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
                                        Bundle.MSG_RX_ALREADY_EXISTS)
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
                    //test if the combination type and new version already
                    // exists in the table
                    boolean exists = false;
                    int i = 0;
                    CTInfo temp = null;
                    while (i < extendedRxs.size()) {
                        if(i==row) {
                            i++;
                            continue;
                        }
                        temp = (CTInfo) extendedRxs.get(i);
                        if ((temp.getCTType().equals(cti.getCTType())) &&
                                (temp.getVerCT().equals(val))) {
                            exists = true;
                        }
                        i++;
                    }
                    if (!exists) {
                        cti.setVerCT(val);
                    } else {
                        JOptionPane.showMessageDialog(
                                null, Bundle.getMessage(
                                        Bundle.MSG_RX_ALREADY_EXISTS)
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
            case 2:
                if (val.equals("true")) {
                    //add previous preferred to rxs
                    if (dataStore.getRxPref() != null) {
                        rxs.add(dataStore.getRxPref());
                    }
                    //set the rx as preferred
                    dataStore.setRxPref(cti);
                    //remove the rx from rx list
                    rxs.remove(cti);

                    //preapare the list used in table
                    extendedRxs = new ArrayList();
                    if (dataStore.getRxPref() != null) {
                        extendedRxs.add(dataStore.getRxPref());
                    }
                    extendedRxs.addAll(rxs);
                    this.fireTableDataChanged();
                }
                break;
        }
    }

    /**
     * Adds a rx to the selected datastore
     *
     * @return the position in table where the new rx was added
     */
    public int addRX() {
        //find the first "New Type i" that does not exists in the table
        boolean exists = true;
        String name = Bundle.getMessage(Bundle.NEW_TYPE);
        String typeName = name;
        CTInfo temp = null;
        int nr = 1;
        while (exists) {
            typeName = name + " " + nr;
            exists = false;
            for (int i = 0; i < extendedRxs.size(); i++) {
                temp = (CTInfo) extendedRxs.get(i);
                if (temp.getCTType().equals(typeName)) {
                    exists = true;
                }
            }
            nr++;
        }

        CTInfo newCTI = new CTInfo(
                typeName, Bundle.getMessage(Bundle.NEW_VERSION));
        //if rxPref is null put the new CTI as rx pref
        if (dataStore.getRxPref() == null) {
            dataStore.setRxPref(newCTI);
        } else {
            //else add it to rx list
            rxs.add(newCTI);
        }

        extendedRxs = new ArrayList();
        extendedRxs.add(dataStore.getRxPref());
        extendedRxs.addAll(rxs);

        fireTableDataChanged();
        return extendedRxs.indexOf(newCTI);
    }

    /**
     * Removes the selected rx
     *
     * @param row the row in the table of the rx to be deleted
     */
    public void remRX(int row) {
        if (row > 0) {
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(Bundle.MSG_DELETE_RX),
                    Bundle.getMessage(Bundle.MSG_DELETE_RX_TITLE),
                    JOptionPane.YES_NO_OPTION);

            if (answer == 0) {
                rxs.remove(row - 1);
                extendedRxs.remove(row);
            }
        }
        if (row == 0) {
            JOptionPane.showMessageDialog(
                    null, Bundle.getMessage(
                            Bundle.MSG_RX_PREFERRED_DELETE));
        }
        fireTableDataChanged();
    }
}
