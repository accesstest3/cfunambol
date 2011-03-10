/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.admin.settings.panels;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import com.funambol.admin.util.Bundle;

import com.funambol.framework.server.Sync4jSource;

import com.funambol.server.engine.Sync4jStrategy;


/**
 * Table model for the resolution table in Sync4jStrategyPanel
 *
 * @version $Id: ResolutionTableModel.java,v 1.9 2007-11-28 10:28:17 nichele Exp $
 */
public class ResolutionTableModel extends AbstractTableModel {

    // ------------------------------------------------------------ Constants
    private final static String[] columns = {
            Bundle.getMessage(Bundle.STRATEGY_COL_NAME),
            Bundle.getMessage(Bundle.STRATEGY_OPTION_DEFAULT_VALUE),
            Bundle.getMessage(Bundle.STRATEGY_OPTION_SERVER_WINS),
            Bundle.getMessage(Bundle.STRATEGY_OPTION_CLIENT_WINS),
            Bundle.getMessage(Bundle.STRATEGY_OPTION_MERGE_DATA)
    };

    private static Class SERVER_WINS_SYNCSOURCE = null;
    private static Class CLIENT_WINS_SYNCSOURCE = null;
    private static Class MERGEABLE_SYNCSOURCE   = null;

    static {
        try {
            SERVER_WINS_SYNCSOURCE =
                Class.forName("com.funambol.framework.engine.source.ServerWinsSyncSource");
            CLIENT_WINS_SYNCSOURCE =
                Class.forName("com.funambol.framework.engine.source.ClientWinsSyncSource");
            MERGEABLE_SYNCSOURCE =
                Class.forName("com.funambol.framework.engine.source.MergeableSyncSource");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Unable to create static fields (" + ex.toString() + ")");
        }
    }

    // ------------------------------------------------------------ Private data

    /** arrayList with the keys in the displayMap (these are the source uri) */
    private ArrayList keys          = null;

    /** the table for which the instance is model */
    private JTable table            = null;

    /** array of classes....one class for syncsource */
    private Class[] syncSourceClasses = null;

    private List serverWinsSyncSources = null;
    private List clientWinsSyncSources = null;
    private List mergeableSyncSources  = null;

    private RowData[] rows = null;

    // ------------------------------------------------------------ Constructor

    /**
     * Creates a table model for the table with the configuration of Sync4jStrategy
     *
     * @param obj the object with the cnfiguration of server's strategy
     */
    public ResolutionTableModel(JTable         table,
                                int[]          resolutions,
                                Sync4jSource[] sync4jSources,
                                Class[]        syncSourceClasses) {
        super();
        this.table = table;
        keys = new ArrayList();

        for(int i=0; i<sync4jSources.length; i++) {
            keys.add(createDisplayName(sync4jSources[i], syncSourceClasses[i]));
        }

        this.syncSourceClasses = syncSourceClasses;

        loadSyncSourceTypes(sync4jSources, syncSourceClasses);

        initRows(resolutions);
    }

    // ---------------------------------------------------------- Public Methods

    public int[] getResolutions() {
        RowData rowData = null;
        int len = keys.size();
        int[] resolutions = new int[len];
        for (int i=0; i<len; i++) {
            rowData = rows[i];
            if (rowData.isDefaultSelected()) {
                resolutions[i] = -1;
            } else if (rowData.isServerWinsSelected()) {
                resolutions[i] = Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS;
            } else if (rowData.isClientWinsSelected()) {
                resolutions[i] = Sync4jStrategy.CONFLICT_RESOLUTION_CLIENT_WINS;
            } else if (rowData.isMergeDataSelected()) {
                resolutions[i] = Sync4jStrategy.CONFLICT_RESOLUTION_MERGE_DATA;
            }
        }
        return resolutions;
    }

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return keys.size();
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

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * Returns true if the cell at row and col is editable. Otherwise,
     * setValueAt on the cell will not change the value of that cell.
     *
     * @param row the row whose value to be queried
     * @param col the column whose value to be queried
     *
     * @return true if the cell is editable, false otherwise
     */
    public boolean isCellEditable(int row, int col) {
        String uri = (String)keys.get(row);
        switch (col) {
            case 0: //Name
                return false;
            case 1:
                // Default resolution
                if (!isAServerWinsSyncSource(uri) &&
                    !isAClientWinsSyncSource(uri) &&
                    !isAMergeableSyncSource (uri)   ) {
                    return true;
                }
                return false;
            case 2:
                // Server wins
                if (isAServerWinsSyncSource(uri)   ||
                    (!isAClientWinsSyncSource(uri) && !isAMergeableSyncSource(uri))) {
                    return true;
                }
                return false;
            case 3:
                // Client wins
                if (isAClientWinsSyncSource(uri)   ||
                    (!isAServerWinsSyncSource(uri) && !isAMergeableSyncSource(uri))) {
                    return true;
                }
                return false;
            case 4:
                // Merge data
                if (isAMergeableSyncSource(uri)   ||
                    (!isAServerWinsSyncSource(uri) && !isAClientWinsSyncSource(uri))) {
                    return true;
                }
                return false;
            default:
                return false;
        }
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

        RowData rowDescription = rows[row];

        if (col == 0) {

            //name
            return rowDescription.name;

        } else if (col==1) {

            //default resolution
            return rowDescription.buttonDefaultValue;

        } else if (col == 2) {

            //server wins
            return rowDescription.buttonServerWins;

        } else if (col == 3) {

            //client wins
            return rowDescription.buttonClientWins;

        } else if (col == 4) {

            //merge data
            return rowDescription.buttonMergeData;

        }
        return "";
    }

    //--------------------------------------------------------- Private methods
    /**
     * Creates the name of the Sync4jSource in format SourceName (Source uri)
     * to be displayed in the table.
     */
    private String createDisplayName(Sync4jSource ss, Class syncSourceClass){
        return ss.getSourceName();
    }

    private void initRows(int[] resolutions) {
        int numRows = keys.size();
        rows = new RowData[numRows];
        for (int i=0; i<numRows; i++) {
            String uri = (String)keys.get(i);
            rows[i] = new RowData(uri,
                                  resolutions[i],
                                  isAServerWinsSyncSource(uri),
                                  isAClientWinsSyncSource(uri),
                                  isAMergeableSyncSource(uri),
                                  table.getBackground());
        }
    }

    /**
     * Loads serverWinsSyncSources, clientWinsSyncSources and mergeableSyncSources
     * according to the given sync4jSources and syncSourceClasses
     */
    private void loadSyncSourceTypes(Sync4jSource[] sync4jSources, Class[] syncSourceClasses) {
        serverWinsSyncSources = new ArrayList();
        clientWinsSyncSources = new ArrayList();
        mergeableSyncSources  = new ArrayList();

        if (sync4jSources == null) {
            sync4jSources = new Sync4jSource[0];
        }
        if (syncSourceClasses == null) {
            syncSourceClasses = new Class[0];
        }
        if (sync4jSources.length != syncSourceClasses.length) {
            throw new IllegalArgumentException("Sync4jSource array incompatible with syncSourceClasses array");
        }
        for (int i=0; i<sync4jSources.length; i++) {
            if (isAServerWinsSyncSource(syncSourceClasses[i])) {
                serverWinsSyncSources.add(sync4jSources[i].getUri());
            } else if (isAClientWinsSyncSource(syncSourceClasses[i])) {
                clientWinsSyncSources.add(sync4jSources[i].getUri());
            } else if (isAMergeableSyncSource(syncSourceClasses[i])) {
                mergeableSyncSources.add(sync4jSources[i].getUri());
            }
        }
    }

    /**
     * Is the given source a serverWinsSyncSources ?
     */
    private boolean isAServerWinsSyncSource(String uri) {
        return serverWinsSyncSources.contains(uri);
    }

    /**
     * Is the given source a clientWinsSyncSources ?
     */
    private boolean isAClientWinsSyncSource(String uri) {
        return clientWinsSyncSources.contains(uri);
    }

    /**
     * Is the given source a mergeableWinsSyncSources ?
     */
    private boolean isAMergeableSyncSource(String uri) {
        return mergeableSyncSources.contains(uri);
    }

    /**
     * Does the given class implement ServerWinsSyncsource ?
     */
    private boolean isAServerWinsSyncSource(Class syncSourceClass) {
        if (syncSourceClass == null) {
            return false;
        }
        Class[] interfaces = syncSourceClass.getInterfaces();

        for (int i=0; i<interfaces.length; i++) {
            if (SERVER_WINS_SYNCSOURCE.equals(interfaces[i])) {
                return true;
            }
        }
        Class parent = syncSourceClass.getSuperclass();
        while (parent != null && !(java.lang.Object.class.equals(parent))) {
            interfaces = parent.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                if (SERVER_WINS_SYNCSOURCE.equals(interfaces[i])) {
                    return true;
                }
            }
            parent = parent.getSuperclass();
        }
        return false;
    }

    /**
     * Does the given class implement ClientWinsSyncsource ?
     */
    private boolean isAClientWinsSyncSource(Class syncSourceClass) {
        if (syncSourceClass == null) {
            return false;
        }
        Class[] interfaces = syncSourceClass.getInterfaces();

        for (int i=0; i<interfaces.length; i++) {
            if (CLIENT_WINS_SYNCSOURCE.equals(interfaces[i])) {
                return true;
            }
        }
        Class parent = syncSourceClass.getSuperclass();
        while (parent != null && !(java.lang.Object.class.equals(parent))) {
            interfaces = parent.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                if (CLIENT_WINS_SYNCSOURCE.equals(interfaces[i])) {
                    return true;
                }
            }
            parent = parent.getSuperclass();
        }

        return false;
    }

    /**
     * Does the given class implement MergeableSyncsource ?
     */
    private boolean isAMergeableSyncSource(Class syncSourceClass) {
        if (syncSourceClass == null) {
            return false;
        }
        Class[] interfaces = syncSourceClass.getInterfaces();

        for (int i=0; i<interfaces.length; i++) {
            if (MERGEABLE_SYNCSOURCE.equals(interfaces[i])) {
                return true;
            }
        }
        Class parent = syncSourceClass.getSuperclass();
        while (parent != null && !(java.lang.Object.class.equals(parent))) {
            interfaces = parent.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                if (MERGEABLE_SYNCSOURCE.equals(interfaces[i])) {
                    return true;
                }
            }
            parent = parent.getSuperclass();
        }

        return false;
    }
}

class RowData {

    public String       name;
    public JRadioButton buttonDefaultValue;
    public JRadioButton buttonServerWins;
    public JRadioButton buttonClientWins;
    public JRadioButton buttonMergeData;

    public RowData(String  name,
                   int     conflictResolution,
                   boolean isServerWins,
                   boolean isClientWins,
                   boolean isMergeable,
                   Color   backgroundColor) {

        this.name = name;
        buttonDefaultValue = new JRadioButton();
        buttonServerWins   = new JRadioButton();
        buttonClientWins   = new JRadioButton();
        buttonMergeData    = new JRadioButton();

        ButtonGroup g1 = new ButtonGroup();

        g1.add(buttonDefaultValue);
        g1.add(buttonServerWins);
        g1.add(buttonClientWins);
        g1.add(buttonMergeData);

        buttonDefaultValue.setHorizontalAlignment(SwingConstants.CENTER);
        buttonServerWins.setHorizontalAlignment(SwingConstants.CENTER);
        buttonClientWins.setHorizontalAlignment(SwingConstants.CENTER);
        buttonMergeData.setHorizontalAlignment(SwingConstants.CENTER);

        buttonDefaultValue.setBackground(backgroundColor);
        buttonServerWins.setBackground(backgroundColor);
        buttonClientWins.setBackground(backgroundColor);
        buttonMergeData.setBackground(backgroundColor);


        if (isServerWins) {
            buttonMergeData.setEnabled(false);
            buttonMergeData.setSelected(false);

            buttonDefaultValue.setEnabled(false);
            buttonDefaultValue.setSelected(false);

            buttonServerWins.setEnabled(true);
            buttonServerWins.setSelected(true);

            buttonClientWins.setEnabled(false);
            buttonClientWins.setSelected(false);

        } else if (isClientWins) {
            buttonMergeData.setEnabled(false);
            buttonMergeData.setSelected(false);

            buttonDefaultValue.setEnabled(false);
            buttonDefaultValue.setSelected(false);

            buttonServerWins.setEnabled(false);
            buttonServerWins.setSelected(false);

            buttonClientWins.setEnabled(true);
            buttonClientWins.setSelected(true);

        } else if (isMergeable) {
            buttonMergeData.setEnabled(true);
            buttonMergeData.setSelected(true);

            buttonDefaultValue.setEnabled(false);
            buttonDefaultValue.setSelected(false);

            buttonServerWins.setEnabled(false);
            buttonServerWins.setSelected(false);

            buttonClientWins.setEnabled(false);
            buttonClientWins.setSelected(false);

        } else  {
            buttonMergeData.setEnabled(false);
            buttonMergeData.setSelected(false);

            buttonDefaultValue.setEnabled(true);
            buttonDefaultValue.setSelected(false);

            buttonServerWins.setEnabled(true);
            buttonServerWins.setSelected(false);

            buttonClientWins.setEnabled(true);
            buttonClientWins.setSelected(false);

            switch (conflictResolution)  {
                case -1:
                    buttonDefaultValue.setSelected(true);
                    break;
                case Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS:
                    buttonServerWins.setSelected(true);
                    break;
                case Sync4jStrategy.CONFLICT_RESOLUTION_CLIENT_WINS:
                    buttonClientWins.setSelected(true);
                    break;
                case Sync4jStrategy.CONFLICT_RESOLUTION_MERGE_DATA:
                    buttonMergeData.setSelected(true);
                    break;
            }
        }
    }

    public boolean isDefaultSelected() {
        return buttonDefaultValue.isSelected();
    }

    public boolean isServerWinsSelected() {
        return buttonServerWins.isSelected();
    }

    public boolean isClientWinsSelected() {
        return buttonClientWins.isSelected();
    }

    public boolean isMergeDataSelected() {
        return buttonMergeData.isSelected();
    }

}

