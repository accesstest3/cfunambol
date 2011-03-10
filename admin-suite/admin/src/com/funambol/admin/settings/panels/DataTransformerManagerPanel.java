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

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import com.funambol.framework.engine.transformer.DataTransformerManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.Map;
import java.util.HashMap;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * The panel for displaying and modifying data transformer managers.
 *
 * @version $Id: DataTransformerManagerPanel.java,v 1.11 2007-11-28 10:28:17 nichele Exp $
 */
public class DataTransformerManagerPanel extends ManagementObjectPanel implements ActionListener{

    // ------------------------------------------------------------ Constants
    private static final String ACTION_SAVE                = "ACTION_SAVE";
    private static final String ACTION_ADD_IN_ITEM         = "ACTION_ADD_IN_ITEM";
    private static final String ACTION_REMOVE_IN_ITEM      = "ACTION_REMOVE_IN_ITEM";
    private static final String ACTION_ADD_OUT_ITEM        = "ACTION_ADD_OUT_ITEM";
    private static final String ACTION_REMOVE_OUT_ITEM     = "ACTION_REMOVE_OUT_ITEM";
    private static final String ACTION_ADD_REQUIRED_ITEM   = "ACTION_ADD_REQUIRED_ITEM";
    private static final String ACTION_REMOVE_REUIRED_ITEM = "ACTION_REMOVE_REUIRED_ITEM";

    // ------------------------------------------------------------ Private data
    private JTable inTable         = null;
    private JTable outTable        = null;
    private JTable requiredTable   = null;
    private JButton saveButton     = null;
    private JButton cancelButton   = null;

    // ------------------------------------------------------------ Constructor
    /** Creates a new instance of DataTransformerManagerPanel */
    public DataTransformerManagerPanel() {

        this.setName(Bundle.getMessage(
                        Bundle.DATA_TRANSFORMERS_MANAGER_PANEL_NAME));
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Set size of the panel
     *
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    /**
     * Tells the panel that it has to update the UI with the values in the
     * management object.
     */
    public void updateForm() {
        //repaint the Form
        init();
        inTable.updateUI();
        outTable.updateUI();
        requiredTable.updateUI();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if (ACTION_ADD_IN_ITEM.equals(e.getActionCommand())) {
            //add a new item into incoming table
            int poz=((DataTransformerManagerTableModel)inTable.getModel()).addItem();
            //select the new row and scroll to the line
            inTable.getSelectionModel().addSelectionInterval(poz,poz);
            int rowPoz=inTable.getRowHeight()*(poz+1);
            inTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if(ACTION_REMOVE_IN_ITEM.equals(e.getActionCommand())) {
            //remove seleced row from incoming table
            int selectedIndex = inTable.getSelectedRow();
            if (inTable.isEditing()) {
                inTable.getCellEditor().stopCellEditing();
            }
            ((DataTransformerManagerTableModel)inTable.getModel()).removeItem(selectedIndex);
        } else if(ACTION_ADD_OUT_ITEM.equals(e.getActionCommand())) {
            //add a new item into outgoing table
            int poz=((DataTransformerManagerTableModel)outTable.getModel()).addItem();
            //select the new row and scroll to the line
            outTable.getSelectionModel().addSelectionInterval(poz,poz);
            int rowPoz=outTable.getRowHeight()*(poz+1);
            outTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if(ACTION_REMOVE_OUT_ITEM.equals(e.getActionCommand())) {
            //remove seleced row from outgoing table
            int selectedIndex = outTable.getSelectedRow();
            if(outTable.isEditing()) {
                outTable.getCellEditor().stopCellEditing();
            }
            ((DataTransformerManagerTableModel)outTable.getModel()).removeItem(selectedIndex);
        } else if(ACTION_ADD_REQUIRED_ITEM.equals(e.getActionCommand())) {
            //add a new item into outgoing table
            int poz=((DataTransformerManagerTableModel)requiredTable.getModel()).addItem();
            //select the new row and scroll to the line
            requiredTable.getSelectionModel().addSelectionInterval(poz,poz);
            int rowPoz=requiredTable.getRowHeight()*(poz+1);
            requiredTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if(ACTION_REMOVE_REUIRED_ITEM.equals(e.getActionCommand())) {
            //remove seleced row from outgoing table
            int selectedIndex = requiredTable.getSelectedRow();
            if(requiredTable.isEditing()) {
                requiredTable.getCellEditor().stopCellEditing();
            }
            ((DataTransformerManagerTableModel)requiredTable.getModel()).removeItem(selectedIndex);
        }
    }

    // --------------------------------------------------------- Private Methods
    /**
     * Builds all the panel's controls
     */
    private void init() {
        this.removeAll();
        this.setLayout(null);

        JPanel layer=new JPanel();
        layer.setLayout(null);
        layer.setBounds(new Rectangle(this.getPreferredSize()));

        JLabel labelTitle = new JLabel(Bundle.getMessage(Bundle.DTM_LABEL_TITLE));
        labelTitle.setFont(GuiFactory.titlePanelFont);
        labelTitle.setBounds(new Rectangle(14, 5, 316, 28));
        labelTitle.setBorder(new TitledBorder(""));

        layer.add(labelTitle);

        //panel for transformers for ingoing items
        JPanel incomingPanel=new JPanel();
        incomingPanel.setLayout(null);
        incomingPanel.setBounds(new Rectangle(14,50,316,150));
        TitledBorder tb1=new TitledBorder(Bundle.getMessage(Bundle.DTM_LABEL_INCOMING));
        tb1.setTitleFont(GuiFactory.defaultTableHeaderFont);
        incomingPanel.setBorder(tb1);

        //get the in properties map and put it into tableModel
        DataTransformerManager dtm=(DataTransformerManager)mo.getObject();

        //add components on incomingPanel
        if(dtm.getDataTransformersIn()==null) {
            dtm.setDataTransformersIn(new HashMap());
        }
        DataTransformerManagerTableModel itm=new DataTransformerManagerTableModel(
                dtm.getDataTransformersIn(),
                DataTransformerColumnsNames.TRANSFORMER_ITEMS_COLUMNS);
        inTable=new JTable(itm);
        inTable.setVisible(true);
        TableColumn col1=inTable.getColumn(inTable.getColumnName(0));
        col1.setMaxWidth(70);
        //make the table stop cell editing when it loses focus
        inTable.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (inTable.isEditing()) {
                    // stop cell editing
                    inTable.getCellEditor().stopCellEditing();
                }
            }
        });
        inTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((DefaultCellEditor)inTable.getDefaultEditor(
                new Object().getClass())).setClickCountToStart(0);
        inTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point p = e.getPoint();
                    int row = inTable.rowAtPoint(p);
                    int column = inTable.columnAtPoint(p);
                    inTable.getSelectionModel().addSelectionInterval(row,row);
                    inTable.editCellAt(row,column);
                }
            }
           public void mousePressed(MouseEvent e){}
           public void mouseReleased(MouseEvent e){}
           public void mouseEntered(MouseEvent e){}
           public void mouseExited(MouseEvent e){}
        });

        JScrollPane scrollPane1 = new JScrollPane(inTable);
        scrollPane1.setBounds(10, 30, 294, 105);
        incomingPanel.add(scrollPane1);

        //put add/remove buttons
        JButton addInItem = new JButton("+");
        addInItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addInItem.setMargin(new Insets(0, 0, 0, 0));
        addInItem.setBounds(260, 10, 18, 18);
        addInItem.addActionListener(this);
        addInItem.setActionCommand(ACTION_ADD_IN_ITEM);
        incomingPanel.add(addInItem);
        JButton remInItem = new JButton("-");
        remInItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remInItem.setMargin(new Insets(0, 0, 0, 0));
        remInItem.setBounds(280, 10, 18, 18);
        remInItem.addActionListener(this);
        remInItem.setActionCommand(ACTION_REMOVE_IN_ITEM);
        incomingPanel.add(remInItem);

        layer.add(incomingPanel);

        //panel for transformers for outgoing items
        JPanel outgoingPanel=new JPanel();
        outgoingPanel.setLayout(null);
        outgoingPanel.setBounds(new Rectangle(14,220,316,150));
        TitledBorder tb2=new TitledBorder(Bundle.getMessage(Bundle.DTM_LABEL_OUTGOING));
        tb2.setTitleFont(GuiFactory.defaultTableHeaderFont);
        outgoingPanel.setBorder(tb2);

        //add components on outgoingPanel
        if(dtm.getDataTransformersOut()==null) {
            dtm.setDataTransformersOut(new HashMap());
        }
        DataTransformerManagerTableModel otm=new DataTransformerManagerTableModel(
                dtm.getDataTransformersOut(),
                DataTransformerColumnsNames.TRANSFORMER_ITEMS_COLUMNS);
        this.outTable=new JTable(otm);
        outTable.setVisible(true);
        TableColumn c1=outTable.getColumn(outTable.getColumnName(0));
        c1.setMaxWidth(70);
        //make the table stop cell editing when it loses focus
        outTable.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (outTable.isEditing()) {
                    // stop cell editing
                    outTable.getCellEditor().stopCellEditing();
                }
            }
        });
        outTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((DefaultCellEditor)outTable.getDefaultEditor(
                new Object().getClass())).setClickCountToStart(0);
        outTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point p = e.getPoint();
                    int row = outTable.rowAtPoint(p);
                    int column = outTable.columnAtPoint(p);
                    outTable.getSelectionModel().addSelectionInterval(row,row);
                    outTable.editCellAt(row,column);
                }
           }

           public void mousePressed(MouseEvent e){}
           public void mouseReleased(MouseEvent e){}
           public void mouseEntered(MouseEvent e){}
           public void mouseExited(MouseEvent e){}
        });


        JScrollPane scrollPane1Out = new JScrollPane(outTable);
        scrollPane1Out.setBounds(10, 30, 294, 105);
        outgoingPanel.add(scrollPane1Out);

        //put add/remove buttons
        JButton addOutItem = new JButton("+");
        addOutItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addOutItem.setMargin(new Insets(0, 0, 0, 0));
        addOutItem.setBounds(260, 10, 18, 18);
        addOutItem.addActionListener(this);
        addOutItem.setActionCommand(ACTION_ADD_OUT_ITEM);
        outgoingPanel.add(addOutItem);
        JButton remOutItem = new JButton("-");
        remOutItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remOutItem.setMargin(new Insets(0, 0, 0, 0));
        remOutItem.setBounds(280, 10, 18, 18);
        remOutItem.addActionListener(this);
        remOutItem.setActionCommand(ACTION_REMOVE_OUT_ITEM);
        outgoingPanel.add(remOutItem);

        layer.add(outgoingPanel);

        //panel for transformers for transformations required
        JPanel transfPanel=new JPanel();
        transfPanel.setLayout(null);
        transfPanel.setBounds(new Rectangle(14,390,316,150));
        TitledBorder tb3=new TitledBorder(Bundle.getMessage(Bundle.DTM_LABEL_TRANSF_REQUIRED));
        tb3.setTitleFont(GuiFactory.defaultTableHeaderFont);
        transfPanel.setBorder(tb3);

        //add components on transfPanel
        if(dtm.getSourceUriTrasformationsRequired()==null) {
            dtm.setSourceUriTrasformationsRequired(new HashMap());
        }

        DataTransformerManagerTableModel ttm=new DataTransformerManagerTableModel(
                dtm.getSourceUriTrasformationsRequired(),
                DataTransformerColumnsNames.TRANSFORMATION_REQUIRED_COLUMNS);
        requiredTable=new JTable(ttm);
        requiredTable.setVisible(true);
        //make the table stop cell editing when it loses focus
        requiredTable.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (requiredTable.isEditing()) {
                    // stop cell editing
                    requiredTable.getCellEditor().stopCellEditing();
                }
            }
        });
        requiredTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((DefaultCellEditor)requiredTable.getDefaultEditor(
                new Object().getClass())).setClickCountToStart(0);
        requiredTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point p = e.getPoint();
                    int row = requiredTable.rowAtPoint(p);
                    int column = requiredTable.columnAtPoint(p);
                    requiredTable.getSelectionModel().addSelectionInterval(row,row);
                    requiredTable.editCellAt(row,column);
                }
           }

           public void mousePressed(MouseEvent e){}
           public void mouseReleased(MouseEvent e){}
           public void mouseEntered(MouseEvent e){}
           public void mouseExited(MouseEvent e){}
        });


        JScrollPane scrollPane1Transf = new JScrollPane(requiredTable);
        scrollPane1Transf.setBounds(10, 30, 294, 105);

        transfPanel.add(scrollPane1Transf);

        //put add/remove buttons
        JButton addRequiredItem = new JButton("+");
        addRequiredItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        addRequiredItem.setMargin(new Insets(0, 0, 0, 0));
        addRequiredItem.setBounds(260, 10, 18, 18);
        addRequiredItem.addActionListener(this);
        addRequiredItem.setActionCommand(ACTION_ADD_REQUIRED_ITEM);
        transfPanel.add(addRequiredItem);
        JButton remRequiredItem = new JButton("-");
        remRequiredItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remRequiredItem.setMargin(new Insets(0, 0, 0, 0));
        remRequiredItem.setBounds(280, 10, 18, 18);
        remRequiredItem.addActionListener(this);
        remRequiredItem.setActionCommand(ACTION_REMOVE_REUIRED_ITEM);
        transfPanel.add(remRequiredItem);

        layer.add(transfPanel);

        ///the save button
        saveButton = new JButton(
                Bundle.getMessage(Bundle.DTM_BUTTON_SAVE));
        saveButton.setBounds(87, 555, 70, 25);
        saveButton.setActionCommand(ACTION_SAVE);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // stop cell editing for all tables
                saveButton.requestFocusInWindow();
                if(requiredTable.isEditing()) {
                    requiredTable.getCellEditor().stopCellEditing();
                }
                if(inTable.isEditing()) {
                    inTable.getCellEditor().stopCellEditing();
                }
                if(outTable.isEditing()) {
                    outTable.getCellEditor().stopCellEditing();
                }
                //put the data from tables to server
                Log.debug("save DataTransformer Manager configuration");
                try {
                    ExplorerUtil.getSyncAdminController()
                        .getServerSettingsController()
                        .setDataTransformerConfiguration(DataTransformerManagerPanel.this.mo);
                    Log.info(Bundle.getMessage(Bundle.MSG_DTM_CONFIGURATION_SAVED));
                }catch (AdminException ex) {
                    String msg = Bundle.getMessage(Bundle.ERROR_SAVING_DTM_CONFIGURATION);
                    NotifyDescriptor desc = new NotifyDescriptor.Message(ex.getMessage());
                    DialogDisplayer.getDefault().notify(desc);

                    Log.error(msg, ex);
                    return;
                }
            }
        });
        layer.add(saveButton);

        ///the cancel button
        cancelButton = new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
        cancelButton.setBounds(167, 555, 70, 25);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelAction();
            }
        });
        layer.add(cancelButton);

        layer.setPreferredSize(this.getPreferredSize());
        layer.setAutoscrolls(true);
        layer.setVisible(true);

        this.add(layer);
        this.setVisible(true);
    }

    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    }

}
