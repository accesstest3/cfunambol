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

import com.funambol.framework.core.CTCap;
import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.Property;
import com.funambol.framework.core.SourceRef;
import com.funambol.framework.core.SyncCap;
import com.funambol.framework.core.SyncType;

/**
 * Table model for the table with the labels of existing datastores
 *
 * @version $Id: DataStoresTableModel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class DataStoresTableModel extends AbstractTableModel {

    // --------------------------------------------------------------- Constants
    private final static String[] columns = {Bundle.getMessage(Bundle.COL_LABEL)};

    // ------------------------------------------------------------ Private data
    private ArrayList dataStores = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a table model for the table with the labels of existing
     * datastores
     *
     * @param dataStores the list with the datastore that will be displayed in
     *                   the table
     */
    public DataStoresTableModel(ArrayList dataStores) {
        this.dataStores = dataStores;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return dataStores.size();
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
        DataStore ds = (DataStore)dataStores.get(row);
        return ds.getDisplayName();
    }

    /**
     * Sets the value in the cell at col and row to value.
     *
     * @param value the new value
     * @param row   the row whose value is to be changed
     * @param col   the column whose value is to be changed
     */
    public void setValueAt(Object value, int row, int col) {
        String val = (String) value;
        if ((val != null) && (!val.equals(""))) {
            DataStore ds = (DataStore) dataStores.get(row);
            ds.setDisplayName(val);
        } else {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getMessage(Bundle.MSG_LABEL_EMPTY)
            );
        }
    }

    /**
     * Add a new DataStore
     *
     * @return the position in table where the new DataStore was added
     */
    public int addDataStore() {
        DataStore newDataStore = new DataStore();
        newDataStore.setSyncCap(new SyncCap());
        newDataStore.setDSMem(null);
        newDataStore.setSourceRef(new SourceRef());
        //add a default ctcap for the datastore
        CTCap ctCaps[] = new CTCap[1];
        Property props[] = new Property[1];
        Property prop = new Property();
        prop.setDisplayName(Bundle.getMessage(Bundle.NEW_PROP));
        prop.setPropName(Bundle.getMessage(Bundle.NEW_PROP));
        props[0] = prop;

        ctCaps[0] = new CTCap(
                new CTInfo(
                        Bundle.getMessage(Bundle.NEW_TYPE),
                        Bundle.getMessage(Bundle.NEW_VERSION)), false, props);
        newDataStore.setCTCaps(ctCaps);
        //add a default sync cap
        SyncType syncTypes[] = new SyncType[1];
        syncTypes[0] = new SyncType(0);
        SyncCap syncCap = new SyncCap(syncTypes);
        newDataStore.setSyncCap(syncCap);

        //find a name like New DataStore i that does not exists in the table nymore
        boolean exists = true;
        DataStore temp = null;
        String name = Bundle.getMessage(Bundle.NEW_DATASTORE);
        String newName = Bundle.getMessage(Bundle.NEW_DATASTORE);
        int nr = 1;
        while (exists) {
            newName = name + nr;
            exists = false;
            for (int i = 0; i < dataStores.size(); i++) {
                temp = (DataStore) dataStores.get(i);
                if (temp.getDisplayName().equals(newName)) {
                    exists = true;
                }
            }
            nr++;
        }
        newDataStore.setDisplayName(newName);
        SourceRef newSourceRef = new SourceRef();
        newSourceRef.setValue(newName);
        newDataStore.setSourceRef(newSourceRef);

        dataStores.add(newDataStore);
        fireTableDataChanged();

        return dataStores.indexOf(newDataStore);
    }

    /**
     * Removes the DataStore from specified row
     *
     * @param row the row of the DataStore to be removed
     */
    public void remDataStore(int row) {
        if ((row >= 0) && (dataStores.size() > 0)) {
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(
                            Bundle.MSG_DELETE_DATASTORE),
                    Bundle.getMessage(
                            Bundle.MSG_DELETE_DATASTORE_TITLE),
                    JOptionPane.YES_NO_OPTION);

            if (answer == 0) {
                dataStores.remove(row);
                fireTableDataChanged();
            }
        }
    }
}
