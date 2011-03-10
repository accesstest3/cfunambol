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

import com.funambol.admin.ui.PanelManager;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.io.Serializable;

import java.util.EventObject;
import java.util.Map;
import java.util.HashMap;

import javax.swing.*;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

import javax.swing.border.TitledBorder;

import javax.swing.table.*;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import com.funambol.framework.filter.AllClause;
import com.funambol.framework.server.Sync4jSource;

import com.funambol.server.engine.Sync4jStrategy;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;


/**
 * Panel panel for displaying and editing the properties of Sync4jStrategy
 *
 * @version $Id: Sync4jStrategyPanel.java,v 1.12 2007-11-28 10:28:17 nichele Exp $
 */
public class Sync4jStrategyPanel extends ManagementObjectPanel
    implements ActionListener {

    // --------------------------------------------------------------- Constants
    private static final String ACTION_SAVE = "ACTION_SAVE";

    private static final String OPTION_RESOLUTION_DEFAULT
            = Bundle.getMessage(Bundle.STRATEGY_OPTION_DEFAULT_VALUE);

    public static final String OPTION_MERGE_DATA =
            Bundle.getMessage(Bundle.STRATEGY_OPTION_MERGE_DATA);

    /** the resolution options for a syncSource */
    private static final String[] CONFLICT_RESOLUTION_OPTIONS = {
                Bundle.getMessage(Bundle.STRATEGY_OPTION_SERVER_WINS),
                Bundle.getMessage(Bundle.STRATEGY_OPTION_CLIENT_WINS)
    };


    // ------------------------------------------------------------ Private data
    private Sync4jStrategy strategy      = null;

    //main components on panel
    private JButton saveButton           = null;
    private JButton cancelButton         = null;
    private JTable resolutionTable       = null;
    private JComboBox defaultComboBox    = null;
    private Sync4jSource[] sync4jSources = null;


    // ------------------------------------------------------------- Constructor
    public Sync4jStrategyPanel() {
        super();
        this.setName(Bundle.getMessage(Bundle.SYNC4JSTRATEGY_PANEL_NAME));
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Set size of the panel
     *
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(550, 500);
    }

    /**
     * Tells the panel that it has to update the UI with the values in the
     * management object.
     */
    public void updateForm() {
        //
        // repaint the Form
        //
        this.strategy=(Sync4jStrategy)this.mo.getObject();
        init();
        this.setVisible(true);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if (ACTION_SAVE.equals(e.getActionCommand())) {
            //
            // save configuration
            // stop cell editing for all tables
            //
            saveButton.requestFocusInWindow();

            if (resolutionTable.isEditing()) {
                resolutionTable.getCellEditor().stopCellEditing();
            }
            //
            // set in the management object the default resolution
            //
            strategy.setDefaultConflictResolution(numericalResolution(
                        (String)this.defaultComboBox.getSelectedItem()
                    ));
            //
            // set in the management object the resolutions from the table
            //
            int[] resolutions =
                    ((ResolutionTableModel)this.resolutionTable.getModel()).getResolutions();

            Map resolutionsMap = new HashMap();
            for(int i=0; i<sync4jSources.length; i++) {
                resolutionsMap.put(sync4jSources[i].getUri(), new Integer(resolutions[i]));
            }
            strategy.setSourceUriConflictsResolution(resolutionsMap);

            //
            //put the data from panel to server
            //
            Log.debug("Save Strategy configuration");
            try {
                ExplorerUtil.getSyncAdminController()
                .getServerSettingsController()
                .setStrategyConfiguration(Sync4jStrategyPanel.this.mo);

                Log.info(Bundle.getMessage(Bundle.MSG_STRATEGY_CONFIGURATION_SAVED));

            } catch (AdminException ex) {
                String msg = Bundle.getMessage(Bundle.ERROR_SAVING_STRATEGY_CONFIGURATION);
                NotifyDescriptor desc =
                        new NotifyDescriptor.Message(ex.getMessage());
                DialogDisplayer.getDefault().notify(desc);

                Log.error(msg, ex);
                return;
            }
        }
    }

// ------------------------------------------------------------- Private Methods
    /**
     * Builds all the panel's controls
     */
    private void init() {
        this.removeAll();
        this.setLayout(null);

        JPanel layer = new JPanel();
        layer.setLayout(null);

        JLabel labelTitle = new JLabel(Bundle.getMessage(Bundle.STRATEGY_PANEL_TITLE));
        labelTitle.setFont(GuiFactory.titlePanelFont);
        labelTitle.setBounds(new Rectangle(14, 5, 500, 28));
        labelTitle.setBorder(new TitledBorder(""));

        layer.add(labelTitle);

        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(new Rectangle(14, 70, 500, 340));

        TitledBorder tb1 = new TitledBorder(
                               Bundle.getMessage(Bundle.STRATEGY_LABEL_CONFLICT_RESOLUTION)
                           );
        tb1.setTitleFont(GuiFactory.defaultTableHeaderFont);
        boxPanel.setBorder(tb1);

        JLabel labelDefault = new JLabel(
                                  Bundle.getMessage(Bundle.STRATEGY_LABEL_DEFAULT_CONFLICT)
                              );

        labelDefault.setFont(GuiFactory.defaultFont);
        labelDefault.setBounds(new Rectangle(14, 25, 216, 28));

        boxPanel.add(labelDefault);
        //
        //the combo box for default option
        //
        defaultComboBox = new JComboBox(CONFLICT_RESOLUTION_OPTIONS);
        defaultComboBox.setBounds(210, 27, 160, 20);
        defaultComboBox.setFont(GuiFactory.defaultFont);
        defaultComboBox.setSelectedItem(
                            stringResolution(strategy.getDefaultConflictResolution())
        );

        boxPanel.add(defaultComboBox);

        String[] syncSourceClassNames = null;
        Class[]  syncSourceClasses    = null;
        try {
            sync4jSources =
                ExplorerUtil.getSyncAdminController().getBusinessDelegate().getSync4jSources(new AllClause());

            String[] ssTypeIds = new String[sync4jSources.length];
            for(int i=0; i<sync4jSources.length; i++) {
                ssTypeIds[i] = sync4jSources[i].getSourceTypeId();
            }
            syncSourceClassNames =
                ExplorerUtil.getSyncAdminController().getBusinessDelegate().getSyncSourceClasses(ssTypeIds);

            ClassLoader cl = ExplorerUtil.getSyncAdminController()
                                         .getSync4jClassLoader();

            syncSourceClasses = new Class[sync4jSources.length];
            for (int i=0; i<syncSourceClassNames.length; i++) {
                syncSourceClasses[i] = Class.forName(syncSourceClassNames[i], true, cl);
            }


        } catch(Exception ex) {
            String msg = Bundle.getMessage(Bundle.ERROR_GETTING_SYNCSOURCES);
            NotifyDescriptor desc = new NotifyDescriptor.Message(ex.getMessage());
            DialogDisplayer.getDefault().notify(desc);

            Log.error(msg, ex);
        }
        //
        // create the array of int with the resolutions
        //
        Map resolutionsMap = strategy.getSourceUriConflictsResolution();
        int[] resolutions = new int[sync4jSources.length];
        for(int i=0; i<sync4jSources.length; i++) {
            Integer res = (Integer)resolutionsMap.get(sync4jSources[i].getUri());
            if(res != null) {
                resolutions[i] = res.intValue();
            } else {
                resolutions[i] = -1;
            }
        }
        //
        // the table
        //
        resolutionTable = new JTable();
        resolutionTable.setModel(new ResolutionTableModel(
                                     resolutionTable,
                                     resolutions,
                                     sync4jSources,
                                     syncSourceClasses
                                 )
                        );

        resolutionTable.setFont(GuiFactory.defaultFont);
        resolutionTable.setVisible(true);

        resolutionTable.setDefaultRenderer(JComponent.class, new ResolutionTableRenderer());
        resolutionTable.setDefaultEditor(JComponent.class, new ResolutionTableEditor());

        JScrollPane scrollPane1 = new JScrollPane(resolutionTable);
        scrollPane1.setBounds(14, 75, 470, 250);
        //
        // column name
        //
        TableColumn colName = resolutionTable.getColumnModel().getColumn(0);
        colName.setMinWidth(120);

        boxPanel.add(scrollPane1);
        layer.add(boxPanel);

        //
        // save  button
        //
        saveButton = new JButton(Bundle.getMessage(Bundle.STRATEGY_BUTTON_SAVE));
        saveButton.setBounds(185, 420, 70, 25);
        saveButton.setActionCommand(ACTION_SAVE);
        saveButton.addActionListener(this);

        //
        // cancel  button
        //
        cancelButton = new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
        cancelButton.setBounds(260, 420, 70, 25);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelAction();
            }
        });

        layer.add(saveButton);
        layer.add(cancelButton);

        layer.setPreferredSize(this.getPreferredSize());
        layer.setAutoscrolls(true);
        layer.setVisible(true);
        layer.setBounds(new Rectangle(this.getPreferredSize()));
        this.add(layer);
        this.setVisible(true);
    }

    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    }

    /**
     * Transforms the String constants for a resolution in the numerical
     * representation of the resolution
     */
    private int numericalResolution(String value) {
        if(Bundle.getMessage(Bundle.STRATEGY_OPTION_CLIENT_WINS).equals(value)) {
            return Sync4jStrategy.CONFLICT_RESOLUTION_CLIENT_WINS;
        } else if (Bundle.getMessage(Bundle.STRATEGY_OPTION_SERVER_WINS).equals(value)) {
            return Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS;
        } else if (Bundle.getMessage(Bundle.STRATEGY_OPTION_MERGE_DATA).equals(value)) {
            return Sync4jStrategy.CONFLICT_RESOLUTION_MERGE_DATA;
        } else {
            return -1;
        }
    }

    /**
     * Transforms the numerical value for a resolution in the string
     * value of the resolution
     */
    private String stringResolution(int value) {
        if(Sync4jStrategy.CONFLICT_RESOLUTION_CLIENT_WINS == value) {
            return Bundle.getMessage(Bundle.STRATEGY_OPTION_CLIENT_WINS);
        } else if(Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS == value) {
            return Bundle.getMessage(Bundle.STRATEGY_OPTION_SERVER_WINS);
        } else if(Sync4jStrategy.CONFLICT_RESOLUTION_MERGE_DATA == value) {
            return Bundle.getMessage(Bundle.STRATEGY_OPTION_MERGE_DATA);
        } else if(value == -1) {
            return Bundle.getMessage(Bundle.STRATEGY_OPTION_DEFAULT_VALUE);
        } else {
            return "";
        }
    }

}

class ResolutionTableEditor implements TableCellEditor, Serializable {

    protected EventListenerList listenerList = new EventListenerList();
    transient ChangeEvent changeEvent = null;

    protected JComponent component = null;
    protected JComponent container = null;

    public Component getComponent() {
        return component;
    }

    public Object getCellEditorValue() {
        return component;
    }

    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    protected void fireEditingStopped() {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {

                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
            }
        }
    }

    protected void fireEditingCanceled() {

        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i>=0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {

                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
            }
        }
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {

        component = (JComponent)value;
        container = table;
        return component;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        //
        // Check MouseEvent.MOUSE_PRESSED
        //
        if(component != null && anEvent instanceof MouseEvent &&
           ((MouseEvent)anEvent).getID() == MouseEvent.MOUSE_PRESSED) {

            Component dispatchComponent = SwingUtilities.getDeepestComponentAt(component, 3, 3 );

            MouseEvent e  = (MouseEvent)anEvent;
            MouseEvent e2 = new MouseEvent(dispatchComponent,
                                           MouseEvent.MOUSE_RELEASED,
                                           e.getWhen() + 100000,
                                           e.getModifiers(),
                                           3,
                                           3,
                                           e.getClickCount(),
                                           e.isPopupTrigger()
                            );
            dispatchComponent.dispatchEvent(e2);
            if(container!=null) {
                dispatchComponent.setBackground(((JTable)container).getSelectionBackground());
            }
            dispatchComponent.repaint();
            e2 = new MouseEvent(dispatchComponent,
                                MouseEvent.MOUSE_CLICKED,
                                e.getWhen() + 100001,
                                e.getModifiers(),
                                3,
                                3,
                                1,
                                e.isPopupTrigger()
                );
            dispatchComponent.dispatchEvent(e2);
        }

        //
        // Repaint the JTable
        //
        if (container != null) {
            container.repaint();
        }
        return true;
    }

}

class ResolutionTableRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable  table,
                                                   Object  value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int     row,
                                                   int     column) {

        JComponent component=(JComponent)value;
        if(table.getSelectedRow()==row) {
            component.setBackground(table.getSelectionBackground());
        } else {
            component.setBackground(Color.WHITE);
        }

        return component;
    }
}
