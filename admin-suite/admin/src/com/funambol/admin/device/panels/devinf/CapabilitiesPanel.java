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

import com.funambol.framework.core.Ext;
import com.funambol.framework.core.VerDTD;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.inventory.DeviceInventory;

import com.funambol.server.config.Configuration;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;

/**
 * The frame for displaying and modifying device capabilities.
 *
 * @version $Id: CapabilitiesPanel.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class CapabilitiesPanel
extends JPanel
implements ListSelectionListener, ActionListener {

    // --------------------------------------------------------------- Constants
    static final String[] BOOLEAN_VALUES = {Boolean.TRUE.toString() ,
                                            Boolean.FALSE.toString()};
    //actions
    private static final String ACTION_SAVE_CAPS      = "ACTION_SAVE_CAPS"     ;
    private static final String ACTION_RESET_CAPS     = "ACTION_RESET_CAPS"    ;
    private static final String ACTION_ADD_DATASTORE  = "ACTION_ADD_DATASTORE" ;
    private static final String ACTION_REM_DATASTORE  = "ACTION_REM_DATASTORE" ;
    private static final String ACTION_ADD_EXT        = "ACTION_ADD_EXT"       ;
    private static final String ACTION_REM_EXT        = "ACTION_REM_EXT"       ;
    private static final String ACTION_ADD_SYNCCAP    = "ACTION_ADD_SYNCCAP"   ;
    private static final String ACTION_REM_SYNCCAP    = "ACTION_REM_SYNCCAP"   ;
    private static final String ACTION_ADD_CTCAP      = "ACTION_ADD_CTCAP"     ;
    private static final String ACTION_REM_CTCAP      = "ACTION_REM_CTCAP"     ;
    private static final String ACTION_ADD_RX         = "ACTION_ADD_RX"        ;
    private static final String ACTION_REM_RX         = "ACTION_REM_RX"        ;
    private static final String ACTION_ADD_TX         = "ACTION_ADD_TX"        ;
    private static final String ACTION_REM_TX         = "ACTION_REM_TX"        ;
    private static final String ACTION_ADD_FILTER_RX  = "ACTION_ADD_FILTER_RX" ;
    private static final String ACTION_REM_FILTER_RX  = "ACTION_REM_FILTER_RX" ;
    private static final String ACTION_ADD_FILTER_CAP = "ACTION_ADD_FILTER_CAP";
    private static final String ACTION_REM_FILTER_CAP = "ACTION_REM_FILTER_CAP";

    // ------------------------------------------------------------ Private data
    private Capabilities caps;
    private String deviceId = null;
    private String currentDataStoreLabel = "";

    //components of the frame
    JPanel layer1 = null;
    //panel with the components of datastore's properties
    JPanel propertiesPanel = null;
    //form's tables
    JTableCap capsPropsTable = null;
    JTable dataStoreTable = null;
    JTable extTable = null;
    JTableCap dsPropTable = null;
    JTable syncCapTable = null;
    JTableCap dsMemTable = null;
    JTable ctCapTable = null;
    JTable rxTable = null;
    JTable txTable = null;
    JTable filterRxTable = null;
    JTable filterCapTable = null;
    JButton extButton = new JButton();

    // ------------------------------------------------------------- Constructor

    /**
     * Constructor for CapabilitiesPanel which is a JPanel. This gets as
     * parameter a Capabilities object
     *
     * @param caps capabilities to be displayed on panel
     * @param deviceId the identifier of the device
     *
     */
    public CapabilitiesPanel(String deviceId) {
        this.deviceId = deviceId;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the capabilities object displayed by the panel
     *
     * @return capabilities object displayed on the panel
     */
    public Capabilities getCapabilities() {
        return this.caps;
    }

    /**
     * Set size of the panel
     *
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(800,600);
    }

    public void loadCaps(Capabilities caps) {
        this.caps = caps;
        buildInterface();
    }

    /**
     * Load empty capabilities
     */
    public void loadEmptyCaps() {
        setEmptyCaps();
        buildInterface();
    }
    // --------------------------------------------------------- Private Methods

    /**
     * Set the devInfo of an empty capabilities
     */
    private void setEmptyCaps() {
        caps = new Capabilities();
        DevInf devInf = new DevInf(new VerDTD("1.2"), "", "", null, "", "",
                                   "", "", "", false, false, false,
                                   new DataStore[0], null, new Ext[0]);

        caps.setDevInf(devInf);
    }


    /**
     * Builds all the interface's controls
     */
    private void buildInterface() {
        this.setLayout(new BorderLayout());
        this.setName(Bundle.getMessage(Bundle.CAPABILITIES_DETAILS_PANEL_NAME));

        CapabilitiesPropsTableModel capsPropsTM    = null;
        DataStoresTableModel        dsTM           = null;
        ExtTableModel               extTM          = null;
        ExtTableEditor              extTableEditor = null;

        if (caps == null || caps.getDevInf() == null) {

            capsPropsTM    = new CapabilitiesPropsTableModel(null);
            dsTM           = new DataStoresTableModel       (new ArrayList());
            extTM          = new ExtTableModel              (new ArrayList(), extButton);
            extTableEditor = new ExtTableEditor             (new ArrayList());

        } else {
            DevInf devInf = caps.getDevInf();

            capsPropsTM    = new CapabilitiesPropsTableModel(devInf         );
            dsTM           = new DataStoresTableModel(devInf.getDataStores());
            extTM          = new ExtTableModel(devInf.getExts(), extButton  );
            extTableEditor = new ExtTableEditor(devInf.getExts()            );
        }

        layer1 = new JPanel();
        layer1.setLayout(null);

        //device capabilities label
        JPanel layer2 = new JPanel();
        layer2.setLayout(null);
        layer2.setBounds(0, 0, 200, 580);
        JLabel labelCaps = new JLabel(
                Bundle.getMessage(Bundle.LABEL_DEVICE_CAPABILITIES));
        labelCaps.setFont(GuiFactory.titlePanelFont);
        labelCaps.setBounds(10, 10, 180, 20);
        labelCaps.setBorder(new TitledBorder(""));
        layer2.add(labelCaps);

        //deviceid label
        JLabel labelDevId = new JLabel(
                Bundle.getMessage(Bundle.LABEL_DEVICE_ID) + "  " + this.deviceId);
        labelDevId.setFont(GuiFactory.defaultFont);
        labelDevId.setBounds(10, 30, 220, 20);
        layer2.add(labelDevId);

        //properties label
        JLabel labelCapsProps = new JLabel(
                Bundle.getMessage(Bundle.LABEL_PROPERTIES));
        labelCapsProps.setFont(GuiFactory.defaultTableHeaderFont);
        labelCapsProps.setBounds(10, 60, 220, 20);
        layer2.add(labelCapsProps);

        //
        //Load capabilities if existing
        //
        capsPropsTable = new JTableCap(capsPropsTM);
        capsPropsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //create a new row editor model to deal with row specific editors
        RowEditorModel rm = new RowEditorModel();
        //set the row editor to the table
        capsPropsTable.setRowEditorModel(rm);
        DefaultCellEditor booleanEditor = new DefaultCellEditor(
                new JComboBox(BOOLEAN_VALUES));
        // tell the RowEditorModel to use booleanEditor for row 6,7,8
        rm.addEditorForRow(6, booleanEditor);
        rm.addEditorForRow(7, booleanEditor);
        rm.addEditorForRow(8, booleanEditor);

        JScrollPane capsPropsScrollPane = new JScrollPane(capsPropsTable);
        capsPropsScrollPane.setBounds(10, 80, 170, 163);
        layer2.add(capsPropsScrollPane);

        //datastores label
        JLabel labelDataStores = new JLabel(
                Bundle.getMessage(Bundle.LABEL_DATASTORES));
        labelDataStores.setFont(GuiFactory.defaultTableHeaderFont);
        labelDataStores.setBounds(10, 260, 220, 20);
        layer2.add(labelDataStores);
        //datastore add/remove buttons
        JButton addDataStore = new JButton("+");
        addDataStore.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addDataStore.setMargin(new Insets(0, 0, 0, 0));
        addDataStore.setBounds(140, 260, 18, 18);
        addDataStore.setActionCommand(ACTION_ADD_DATASTORE);
        addDataStore.addActionListener(this);
        //layer2.add(addDataStore);

        JButton remDataStore = new JButton("-");
        remDataStore.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remDataStore.setMargin(new Insets(0, 0, 0, 0));
        remDataStore.setBounds(160, 260, 18, 18);
        remDataStore.setActionCommand(ACTION_REM_DATASTORE);
        remDataStore.addActionListener(this);
        //layer2.add(remDataStore);

        //
        // Load DataStore if existing
        //
        dataStoreTable = new JTable(dsTM);

        //code for selection listener
        dataStoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel dataStoreSelectionModel = dataStoreTable.getSelectionModel();
        dataStoreSelectionModel.addListSelectionListener(this);
        //end code for selection listener
        //select first item in the datastore list
        dataStoreTable.getSelectionModel().addSelectionInterval(0, 0);

        JScrollPane scrollPane1 = new JScrollPane(dataStoreTable);
        scrollPane1.setBounds(10, 280, 170, 100);
        layer2.add(scrollPane1);

        //EXT
        JLabel labelExt = new JLabel(Bundle.getMessage(Bundle.LABEL_EXT));
        labelExt.setFont(GuiFactory.defaultTableHeaderFont);
        labelExt.setBounds(10, 390, 220, 20);
        layer2.add(labelExt);
        //ext add/remove buttons
        JButton addExt = new JButton("+");
        addExt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addExt.setMargin(new Insets(0, 0, 0, 0));
        addExt.setBounds(140, 390, 18, 18);
        addExt.setActionCommand(ACTION_ADD_EXT);
        addExt.addActionListener(this);
        //layer2.add(addExt);

        JButton remExt = new JButton("-");
        remExt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remExt.setMargin(new Insets(0, 0, 0, 0));
        remExt.setBounds(160, 390, 18, 18);
        remExt.setActionCommand(ACTION_REM_EXT);
        remExt.addActionListener(this);
        //layer2.add(remExt);

        //
        // Load Ext table
        //
        extTable = new JTable(extTM);
        extTable.setDefaultEditor(JButton.class, extTableEditor);
        extTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());
        extTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPaneExt = new JScrollPane(extTable);
        scrollPaneExt.setBounds(10, 410, 170, 150);
        layer2.add(scrollPaneExt);

        //save buttom
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);
        buttonsPanel.setBounds(0, 570, 800, 600);

        JButton saveCapsButton = new JButton(
                Bundle.getMessage(Bundle.BUTTON_SAVE));
        saveCapsButton.setBounds(315, 0, 80, 25);
        saveCapsButton.setActionCommand(ACTION_SAVE_CAPS);
        saveCapsButton.addActionListener(this);
        //buttonsPanel.add(saveCapsButton);

        //reset button
        JButton resetCapsButton = new JButton(
                Bundle.getMessage(Bundle.BUTTON_RESET));
        resetCapsButton.setBounds(405, 0, 80, 25);
        resetCapsButton.setActionCommand(ACTION_RESET_CAPS);
        resetCapsButton.addActionListener(this);
        //buttonsPanel.add(resetCapsButton);

        layer2.setVisible(true);
        buttonsPanel.setVisible(true);

        layer1.add(layer2);
        layer1.add(buttonsPanel);

        layer1.setPreferredSize(this.getPreferredSize());
        layer1.setAutoscrolls(true);

        this.add(layer1);
    }

    /**
     * ListSelectionListener method for dataStoreTable. This method is called
     * when the selection from DataStoreTable changes
     *
     * @param e list selection event
     */
    public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;
        if (null == caps || null == caps.getDevInf()) {
            return;
        }
        //stop edditing in all tables
        tablesStopEditing();

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!lsm.isSelectionEmpty()) {
            //set the goupBox's label
            if (lsm.getMinSelectionIndex() < caps.getDevInf().getDataStores().size()) {
                this.currentDataStoreLabel = ((DataStore) caps.getDevInf()
                        .getDataStores().get(
                            lsm.getMinSelectionIndex())).getDisplayName();
                if (propertiesPanel == null) {
                    buildDataStorePropertiesPanel();
                    layer1.add(propertiesPanel);
                }
                propertiesPanel.setVisible(true);
                propertiesPanel.setFont(GuiFactory.defaultTableHeaderFont);
                propertiesPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Datastore: " + currentDataStoreLabel),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                //repaint the tables
                fireDataStorePropertiesTables(lsm.getMinSelectionIndex());
            }
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent event) {
        if (ACTION_SAVE_CAPS.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            try {
                ExplorerUtil.getSyncAdminController()
                            .getDevicesController()
                            .saveDeviceCapabilities(caps, deviceId);

                fireCapabilitiesPropertiesTables();
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            }
        } else if (ACTION_RESET_CAPS.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            try {
                caps = ExplorerUtil.getSyncAdminController()
                                   .getDevicesController()
                                   .getDeviceCapabilities(deviceId);

                fireCapabilitiesPropertiesTables();
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            }

        } else if (ACTION_ADD_DATASTORE.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add new datastore
            int position=((DataStoresTableModel) dataStoreTable.getModel())
                            .addDataStore();
            dataStoreTable.requestFocus();
            dataStoreTable.getSelectionModel()
                            .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=dataStoreTable.getRowHeight()*(position+1);
            dataStoreTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_DATASTORE.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected datastore
            int selectedIndex = dataStoreTable.getSelectedRow();
            ((DataStoresTableModel) dataStoreTable.getModel()).remDataStore(selectedIndex);
            if (dataStoreTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                dataStoreTable.getSelectionModel()
                            .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*dataStoreTable.getRowHeight();
                dataStoreTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            } else {
                if(propertiesPanel!=null) {
                    propertiesPanel.setVisible(false);
                }
            }
        } else if (ACTION_ADD_EXT.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add new EXT
            int position = ((ExtTableModel) extTable.getModel()).addExt();
            extTable.requestFocus();
            extTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=extTable.getRowHeight()*(position+1);
            extTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_EXT.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected EXT
            int selectedIndex = extTable.getSelectedRow();
            ((ExtTableModel) extTable.getModel()).remExt(selectedIndex);
            if(extTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                extTable.getSelectionModel()
                        .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*extTable.getRowHeight();
                extTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_SYNCCAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add syncCap
            int position = ((SyncCapTableModel) syncCapTable.getModel()).addSyncCap();
            syncCapTable.requestFocus();
            syncCapTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=syncCapTable.getRowHeight()*(position+1);
            syncCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_SYNCCAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected SyncCap
            int selectedIndex = syncCapTable.getSelectedRow();
            ((SyncCapTableModel) syncCapTable.getModel()).remSyncCap(selectedIndex);
            if(syncCapTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                syncCapTable.getSelectionModel()
                            .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*syncCapTable.getRowHeight();
                syncCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_CTCAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add a ctcap
            int position = ((CTCapTableModel) ctCapTable.getModel()).addCTCap();
            ctCapTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=ctCapTable.getRowHeight()*(position+1);
            ctCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_CTCAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected ctcap
            int selectedIndex = ctCapTable.getSelectedRow();
            ((CTCapTableModel) ctCapTable.getModel()).remCTCap(selectedIndex);
            if(ctCapTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                ctCapTable.getSelectionModel()
                          .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*ctCapTable.getRowHeight();
                ctCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_RX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add a RX
            int position = ((RXTableModel) rxTable.getModel()).addRX();
            rxTable.requestFocus();
            rxTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=rxTable.getRowHeight()*(position+1);
            rxTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_RX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected RX
            int selectedIndex = rxTable.getSelectedRow();
            ((RXTableModel) rxTable.getModel()).remRX(selectedIndex);
            if(rxTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                rxTable.getSelectionModel()
                       .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*rxTable.getRowHeight();
                rxTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_TX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add a TX
            int position = ((TXTableModel) txTable.getModel()).addTX();
            txTable.requestFocus();
            txTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=txTable.getRowHeight()*(position+1);
            txTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_TX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //remove selected TX
            int selectedIndex = txTable.getSelectedRow();
            ((TXTableModel) txTable.getModel()).remTX(selectedIndex);
            if(txTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                txTable.getSelectionModel()
                       .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*txTable.getRowHeight();
                txTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_FILTER_RX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add a new filterRX
            int position = ((FilterRXTableModel) filterRxTable.getModel()).addFilterRX();
            filterRxTable.requestFocus();
            filterRxTable.getSelectionModel()
                         .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=filterRxTable.getRowHeight()*(position+1);
            filterRxTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_FILTER_RX.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //removes the selected filterRX
            int selectedIndex = filterRxTable.getSelectedRow();
            ((FilterRXTableModel) filterRxTable.getModel()).remFilterRX(selectedIndex);
            if(filterRxTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                filterRxTable.getSelectionModel()
                             .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*filterRxTable.getRowHeight();
                filterRxTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_FILTER_CAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //add a new filterCap
            int position = ((FilterCapTableModel) filterCapTable.getModel()).addFilterCap();
            filterCapTable.getSelectionModel().addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=filterCapTable.getRowHeight()*(position+1);
            filterCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_FILTER_CAP.equals(event.getActionCommand())) {
            //stop edditing in all tables
            tablesStopEditing();
            //removes the selected filterCap
            int selectedIndex = filterCapTable.getSelectedRow();
            ((FilterCapTableModel) filterCapTable.getModel()).remFilterCap(selectedIndex);
            if(filterCapTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                filterCapTable.getSelectionModel()
                              .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*filterCapTable.getRowHeight();
                filterCapTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        }
    }

    //-----------------------------------------------------------Private Methods
    /**
     * Build the panel for a datastore's properties. The reference to the panel
     * with the properties is made through propertiesPanel field of this class
     */
    private void buildDataStorePropertiesPanel() {
        //DATASTORE PROPERTIES PANEL
        propertiesPanel = new JPanel();
        propertiesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    Bundle.getMessage(Bundle.LABEL_DATASTORE) + " "
                                      + currentDataStoreLabel),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        propertiesPanel.setBounds(200, 60, 580, 500);
        propertiesPanel.setLayout(null);

        //load datastores Properties
        JLabel labelDataStoresProp = new JLabel(
                Bundle.getMessage(Bundle.LABEL_PROPERTIES));
        labelDataStoresProp.setFont(GuiFactory.defaultTableHeaderFont);
        labelDataStoresProp.setBounds(30, 20, 240, 20);
        propertiesPanel.add(labelDataStoresProp);

        DataStorePropTableModel dsPropTM = new DataStorePropTableModel(
            (DataStore) this.caps.getDevInf().getDataStores().get(0)
        );
        dsPropTable = new JTableCap(dsPropTM);
        dsPropTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //create a new row editor model to deal with row specific editors
        RowEditorModel rmDsProp = new RowEditorModel();
        //set the row editor to the table
        dsPropTable.setRowEditorModel(rmDsProp);
        DefaultCellEditor booleanEditorDsProp = new DefaultCellEditor(
                new JComboBox(BOOLEAN_VALUES));
        // tell the RowEditorModel to use booleanEditor for row 3,4
        rmDsProp.addEditorForRow(3, booleanEditorDsProp);
        rmDsProp.addEditorForRow(4, booleanEditorDsProp);

        JScrollPane dsPropSCroolPane = new JScrollPane(dsPropTable);
        dsPropSCroolPane.setBounds(30, 40, 240, 100);
        propertiesPanel.add(dsPropSCroolPane);

        //sync capabilities
        JLabel labelSyncCap = new JLabel(Bundle.getMessage(Bundle.LABEL_SYNCCAP));
        labelSyncCap.setFont(GuiFactory.defaultTableHeaderFont);
        labelSyncCap.setBounds(30, 145, 240, 20);
        propertiesPanel.add(labelSyncCap);
        //sync cap add/remove buttons
        JButton addSyncCap = new JButton("+");
        addSyncCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addSyncCap.setMargin(new Insets(0, 0, 0, 0));
        addSyncCap.setBounds(230, 145, 18, 18);
        addSyncCap.setActionCommand(ACTION_ADD_SYNCCAP);
        addSyncCap.addActionListener(this);
        //propertiesPanel.add(addSyncCap);

        JButton remSyncCap = new JButton("-");
        remSyncCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remSyncCap.setMargin(new Insets(0, 0, 0, 0));
        remSyncCap.setBounds(250, 145, 18, 18);
        remSyncCap.setActionCommand(ACTION_REM_SYNCCAP);
        remSyncCap.addActionListener(this);
        //propertiesPanel.add(remSyncCap);

        syncCapTable = new JTable(new SyncCapTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0)));
        syncCapTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane21 = new JScrollPane(syncCapTable);
        scrollPane21.setBounds(30, 165, 240, 100);
        propertiesPanel.add(scrollPane21);

        //DSMem
        JLabel labelDSMem = new JLabel(Bundle.getMessage(Bundle.LABEL_DSMEM));
        labelDSMem.setFont(GuiFactory.defaultTableHeaderFont);
        labelDSMem.setBounds(30, 270, 240, 20);
        propertiesPanel.add(labelDSMem);

        DSMemTableModel dsMemTM = new DSMemTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0));
        dsMemTable = new JTableCap(dsMemTM);
        dsMemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //create a new row editor model to deal with row specific editors
        RowEditorModel dsMemProp = new RowEditorModel();
        //set the row editor to the table
        dsMemTable.setRowEditorModel(dsMemProp);
        DefaultCellEditor booleanEditorDsMem = new DefaultCellEditor(
                new JComboBox(BOOLEAN_VALUES));
        // tell the RowEditorModel to use booleanEditor for row 0
        dsMemProp.addEditorForRow(0, booleanEditorDsMem);

        JScrollPane scrollPane17 = new JScrollPane(dsMemTable);
        scrollPane17.setBounds(30, 290, 240, 80);
        propertiesPanel.add(scrollPane17);

        //ctcap
        JLabel labelCTCap = new JLabel(Bundle.getMessage(Bundle.LABEL_CTCAP));
        labelCTCap.setFont(GuiFactory.defaultTableHeaderFont);
        labelCTCap.setBounds(310, 270, 240, 20);
        propertiesPanel.add(labelCTCap);
        //add/remove buttons
        JButton addCTCap = new JButton("+");
        addCTCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addCTCap.setMargin(new Insets(0, 0, 0, 0));
        addCTCap.setBounds(510, 270, 18, 18);
        addCTCap.setActionCommand(ACTION_ADD_CTCAP);
        addCTCap.addActionListener(this);
        //propertiesPanel.add(addCTCap);

        JButton remCTCap = new JButton("-");
        remCTCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remCTCap.setMargin(new Insets(0, 0, 0, 0));
        remCTCap.setBounds(530, 270, 18, 18);
        remCTCap.setActionCommand(ACTION_REM_CTCAP);
        remCTCap.addActionListener(this);
        //propertiesPanel.add(remCTCap);

        //ctCap table
        JButton but = new JButton("");
        CTCapTableModel ctCapTM = new CTCapTableModel(
            (DataStore)caps.getDevInf().getDataStores().get(0), but
        );
        ctCapTable = new JTable(ctCapTM);
        ctCapTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //add column to show properties and params
        CTCapPropertiesTableEditor ctCaptableEditor =
            new CTCapPropertiesTableEditor(
                ((DataStore) caps.getDevInf().getDataStores().get(0)).getCTCaps()
            );
        ctCapTable.setDefaultEditor(JButton.class, ctCaptableEditor);
        ctCapTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());

        TableColumn column = ctCapTable.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES)));

        JScrollPane scrollPane16 = new JScrollPane(ctCapTable);
        scrollPane16.setBounds(310, 290, 240, 80);
        propertiesPanel.add(scrollPane16);

        //RX
        JLabel labelRX = new JLabel(Bundle.getMessage(Bundle.LABEL_RX));
        labelRX.setFont(GuiFactory.defaultTableHeaderFont);
        labelRX.setBounds(310, 20, 240, 20);
        propertiesPanel.add(labelRX);

        //add/remove buttons
        JButton addRx = new JButton("+");
        addRx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addRx.setMargin(new Insets(0, 0, 0, 0));
        addRx.setBounds(510, 20, 18, 18);
        addRx.setActionCommand(ACTION_ADD_RX);
        addRx.addActionListener(this);
        //propertiesPanel.add(addRx);

        JButton remRx = new JButton("-");
        remRx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remRx.setActionCommand(ACTION_REM_RX);
        remRx.addActionListener(this);
        remRx.setMargin(new Insets(0, 0, 0, 0));
        remRx.setBounds(530, 20, 18, 18);
        //propertiesPanel.add(remRx);

        RXTableModel rxTM = new RXTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0));
        rxTable = new JTable(rxTM);
        rxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn columnRX = rxTable.getColumnModel().getColumn(2);
        columnRX.setCellEditor((new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES))));
        JScrollPane rxScrollPanel = new JScrollPane(rxTable);
        rxScrollPanel.setBounds(310, 40, 240, 100);
        propertiesPanel.add(rxScrollPanel);

        //TX
        JLabel labelTX = new JLabel(Bundle.getMessage(Bundle.LABEL_TX));
        labelTX.setFont(GuiFactory.defaultTableHeaderFont);
        labelTX.setBounds(310, 145, 240, 20);
        propertiesPanel.add(labelTX);

        //add/remove buttons
        JButton addTx = new JButton("+");
        addTx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addTx.setMargin(new Insets(0, 0, 0, 0));
        addTx.setBounds(510, 145, 18, 18);
        addTx.setActionCommand(ACTION_ADD_TX);
        addTx.addActionListener(this);
        //propertiesPanel.add(addTx);

        JButton remTx = new JButton("-");
        remTx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remTx.setMargin(new Insets(0, 0, 0, 0));
        remTx.setBounds(530, 145, 18, 18);
        remTx.setActionCommand(ACTION_REM_TX);
        remTx.addActionListener(this);
        //propertiesPanel.add(remTx);

        TXTableModel txTM = new TXTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0));
        txTable = new JTable(txTM);
        txTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn columnTX = txTable.getColumnModel().getColumn(2);
        columnTX.setCellEditor(new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES)));
        JScrollPane txScrollPane1 = new JScrollPane(txTable);
        txScrollPane1.setBounds(310, 165, 240, 100);
        propertiesPanel.add(txScrollPane1);

        //filter RX
        JLabel labelFilterRX = new JLabel(Bundle.getMessage(Bundle.LABEL_FILTER_RX));
        labelFilterRX.setFont(GuiFactory.defaultTableHeaderFont);
        labelFilterRX.setBounds(30, 375, 240, 20);
        propertiesPanel.add(labelFilterRX);

        //add/remove buttons
        JButton addFilterRx = new JButton("+");
        addFilterRx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addFilterRx.setMargin(new Insets(0, 0, 0, 0));
        addFilterRx.setBounds(230, 375, 18, 18);
        addFilterRx.setActionCommand(ACTION_ADD_FILTER_RX);
        addFilterRx.addActionListener(this);
        //propertiesPanel.add(addFilterRx);

        JButton remFilterRx = new JButton("-");
        remFilterRx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remFilterRx.setMargin(new Insets(0, 0, 0, 0));
        remFilterRx.setBounds(250, 375, 18, 18);
        remFilterRx.setActionCommand(ACTION_REM_FILTER_RX);
        remFilterRx.addActionListener(this);
        //propertiesPanel.add(remFilterRx);

        FilterRXTableModel filterRxTM = new FilterRXTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0));
        filterRxTable = new JTable(filterRxTM);
        filterRxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane filterRxScrollPane1 = new JScrollPane(filterRxTable);
        filterRxScrollPane1.setBounds(30, 395, 240, 90);
        propertiesPanel.add(filterRxScrollPane1);

        //filter CAP
        JLabel labelFilterCap = new JLabel(Bundle.getMessage(Bundle.LABEL_FILTER_CAP));
        labelFilterCap.setFont(GuiFactory.defaultTableHeaderFont);
        labelFilterCap.setBounds(310, 375, 240, 20);
        propertiesPanel.add(labelFilterCap);

        //add/remove buttons
        JButton addFilterCap = new JButton("+");
        addFilterCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addFilterCap.setMargin(new Insets(0, 0, 0, 0));
        addFilterCap.setBounds(510, 375, 18, 18);
        addFilterCap.setActionCommand(ACTION_ADD_FILTER_CAP);
        addFilterCap.addActionListener(this);
        //propertiesPanel.add(addFilterCap);

        JButton remFilterCap = new JButton("-");
        remFilterCap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remFilterCap.setMargin(new Insets(0, 0, 0, 0));
        remFilterCap.setBounds(530, 375, 18, 18);
        remFilterCap.setActionCommand(ACTION_REM_FILTER_CAP);
        remFilterCap.addActionListener(this);
        //propertiesPanel.add(remFilterCap);

        //filterCap table
        JButton filterCapButton = new JButton();
        FilterCapTableModel filterCapTM = new FilterCapTableModel(
                (DataStore) caps.getDevInf().getDataStores().get(0), filterCapButton);
        filterCapTable = new JTable(filterCapTM);
        filterCapTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //add column to show properties
        FilterCapTableEditor filterCapTableEditor = new FilterCapTableEditor(
                ((DataStore) caps.getDevInf().getDataStores().get(0)).getFilterCaps());
        filterCapTable.setDefaultEditor(JButton.class, filterCapTableEditor);
        filterCapTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());

        JScrollPane filterCapScrollPane = new JScrollPane(filterCapTable);
        filterCapScrollPane.setBounds(310, 395, 240, 90);
        propertiesPanel.add(filterCapScrollPane);
    }

    /**
     * Repaints all the tables with the properties of the selected datastore
     *
     * @param currentDataStoreIndex the index of the currently selected
     *                              datastore
     */
    private void fireDataStorePropertiesTables(int currentDataStoreIndex) {
        DataStore ds = (DataStore) this.caps.getDevInf().getDataStores()
                            .get(currentDataStoreIndex);

        dsPropTable.setModel(new DataStorePropTableModel(ds));

        syncCapTable.setModel(new SyncCapTableModel(ds));

        dsMemTable.setModel(new DSMemTableModel(ds));

        JButton button = new JButton();
        ctCapTable.setModel(new CTCapTableModel(ds, button));
        CTCapPropertiesTableEditor tableEditor =
                new CTCapPropertiesTableEditor(ds.getCTCaps());
        ctCapTable.setDefaultEditor(JButton.class, tableEditor);
        ctCapTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());
        TableColumn column = ctCapTable.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES)));

        rxTable.setModel(new RXTableModel(ds));
        TableColumn column1 = rxTable.getColumnModel().getColumn(2);
        column1.setCellEditor(new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES)));

        txTable.setModel(new TXTableModel(ds));
        TableColumn column2 = txTable.getColumnModel().getColumn(2);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox(BOOLEAN_VALUES)));

        filterRxTable.setModel(new FilterRXTableModel(ds));

        JButton filterCapButton = new JButton();
        filterCapTable.setModel(new FilterCapTableModel(ds, filterCapButton));
        FilterCapTableEditor filterCapTableEditor =
                new FilterCapTableEditor(ds.getFilterCaps());
        filterCapTable.setDefaultEditor(JButton.class, filterCapTableEditor);
        filterCapTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());
    }

    /**
     * Repaints the tables for capabilities properties, datastore and EXT
     * and hide the datastore's properties
     */
    private void fireCapabilitiesPropertiesTables() {

        if (caps == null || -1 == caps.getId().intValue()) {
            setEmptyCaps();
        }

        capsPropsTable.setModel(new CapabilitiesPropsTableModel(caps.getDevInf()));

        dataStoreTable.setModel(new DataStoresTableModel(
                caps.getDevInf().getDataStores()));
        if (dataStoreTable.getModel().getRowCount() > 0) {
            dataStoreTable.getSelectionModel().addSelectionInterval(0, 0);
        } else {
            if(propertiesPanel!=null) {
                propertiesPanel.setVisible(false);
            }
        }

        JButton extButton = new JButton();
        extTable.setModel(new ExtTableModel(caps.getDevInf().getExts(), extButton));
        ExtTableEditor extTableEditor = new ExtTableEditor(caps.getDevInf().getExts());
        extTable.setDefaultEditor(JButton.class, extTableEditor);
        extTable.setDefaultRenderer(JButton.class, new PropertiesCellRenderer());
    }

    /**
     * When this method is called all the tables on panel stop edditing
     **/
    private void tablesStopEditing() {
        if((capsPropsTable != null)&&(capsPropsTable.isEditing())) {
            capsPropsTable.getCellEditor().stopCellEditing();
        }
        if((dataStoreTable != null)&&(dataStoreTable.isEditing())) {
            dataStoreTable.getCellEditor().stopCellEditing();
        }
        if((extTable != null)&&(extTable.isEditing())) {
            extTable.getCellEditor().stopCellEditing();
        }
        if((dsPropTable != null)&&(dsPropTable.isEditing())) {
            dsPropTable.getCellEditor().stopCellEditing();
        }
        if((syncCapTable != null)&&(syncCapTable.isEditing())) {
            syncCapTable.getCellEditor().stopCellEditing();
        }
        if((dsMemTable != null)&&(dsMemTable.isEditing())) {
            dsMemTable.getCellEditor().stopCellEditing();
        }
        if((ctCapTable != null)&&(ctCapTable.isEditing())) {
            ctCapTable.getCellEditor().stopCellEditing();
        }
        if((rxTable != null)&&(rxTable.isEditing())) {
            rxTable.getCellEditor().stopCellEditing();
        }
        if((txTable != null)&&(txTable.isEditing())) {
            txTable.getCellEditor().stopCellEditing();
        }
        if((filterRxTable != null)&&(filterRxTable.isEditing())) {
            filterRxTable.getCellEditor().stopCellEditing();
        }
        if((filterCapTable != null)&&(filterCapTable.isEditing())) {
            filterCapTable.getCellEditor().stopCellEditing();
        }
    }
}
