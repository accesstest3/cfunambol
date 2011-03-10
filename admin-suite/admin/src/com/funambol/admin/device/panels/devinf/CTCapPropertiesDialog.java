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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;

import org.openide.windows.WindowManager;

import com.funambol.framework.core.CTCap;
import com.funambol.framework.core.Property;
import com.funambol.framework.core.PropParam;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.DeepObjectCloner;

/**
 * Panel that is shown when the properties of a CTCap are edited.
 *
 * @version $Id: CTCapPropertiesDialog.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 */
public class CTCapPropertiesDialog extends JDialog implements ActionListener, WindowListener {

    // --------------------------------------------------------------- Constants
    private static final String ACTION_ADD_PROP                 ="ACTION_ADD_PROP";
    private static final String ACTION_REM_PROP                 ="ACTION_REM_PROP";
    private static final String ACTION_ADD_PROP_VALENUM         ="ACTION_ADD_PROP_VALENUM";
    private static final String ACTION_REM_PROP_VALENUM         ="ACTION_REM_PROP_VALENUM";
    private static final String ACTION_ADD_PROP_PARAM           ="ACTION_ADD_PROP_PARAM";
    private static final String ACTION_REM_PROP_PARAM           ="ACTION_REM_PROP_PARAM";
    private static final String ACTION_ADD_PROP_PARAM_VALENUM   ="ACTION_ADD_PROP_PARAM_VALENUM";
    private static final String ACTION_REM_PROP_PARAM_VALENUM   ="ACTION_REM_PROP_PARAM_VALENUM";
    private static final String ACTION_SAVE                     ="ACTION_SAVE";
    private static final String ACTION_DISCARD                  ="ACTION_DISCARD";

    // ------------------------------------------------------------ Private data
    /**
     * the ctCap for wich this dialog will display properties
     */
    private CTCap ctCap                 = null;
    private ArrayList originalProps     = null;
    /**
     * curent selected ctCap Property
     */
    private Property curentProp         = null;
    //components on panel
    private JTable ctCapPropValEnumTable = null;
    private JTable ctCapPropParamValEnumTable = null;
    private JTable ctCapPropsTable = null;
    private JTable ctCapPropParamsTable = null;
    private JPanel propValEnumPanel = null;
    private JPanel propParamPanel = null;
    private JPanel paramValEnumPanel = null;

    // ------------------------------------------------------------- Constructor
    /**
     * Creates an empty CTCapRropertiesDialog but it doesn't show it. This
     * constructor is private because dialogs must be created using
     * createDialog() static method of this class.
     *
     * @param frame parent frame
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     */
    private CTCapPropertiesDialog(Frame frame, boolean modal) {
        super(WindowManager.getDefault().getMainWindow(),modal);
        this.setResizable(false);
        this.setBounds(0, 0, 780, 550);
        this.setTitle(Bundle.getMessage(Bundle.LABEL_CTCAP_PROPERTIES));
        this.addWindowListener(this);
        this.setLocationRelativeTo(this.getParent());
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Sets the object that is displayed for editing on the dialog
     *
     * @param ctCap the object to be edited
     */
    public void setCTCap(CTCap ctCap) {
        if(ctCap!=null) {
            this.ctCap= ctCap;
            propValEnumPanel=null;
            propParamPanel=null;
            paramValEnumPanel=null;
            //keep a clone of the original properties list
            try {
                originalProps=(ArrayList)DeepObjectCloner.deepClone(
                        ctCap.getProperties());
            } catch(Exception e) {
                //if clone was not possible then original will
                //point to same object as the one that is edited
                originalProps=ctCap.getProperties();
            };
            buildInterface();
        }
    }

    /**
     * Creates a dialog for editing the properties of an CTCap
     *
     * @param table the table from where the dialog is opened
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     *
     * @return new instance of CTCapPropertiesDialog
     */
    public static CTCapPropertiesDialog createDialog(JButton table, boolean modal) {
        return new CTCapPropertiesDialog(
                JOptionPane.getFrameForComponent(table), modal);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if (ACTION_ADD_PROP.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int position=((CTCapPropertiesTableModel)
                            ctCapPropsTable.getModel()).addProperty();
            ctCapPropsTable.requestFocus();
            ctCapPropsTable.getSelectionModel()
                           .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=ctCapPropsTable.getRowHeight()*(position+1);
            ctCapPropsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_PROP.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int selectedIndex = ctCapPropsTable.getSelectedRow();
            ((CTCapPropertiesTableModel) ctCapPropsTable.getModel())
                                               .remProperty(selectedIndex);
            ctCapPropsTable.requestFocus();
            if (ctCapPropsTable.getModel().getRowCount() > 0) {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                ctCapPropsTable.getSelectionModel()
                               .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*ctCapPropsTable.getRowHeight();
                ctCapPropsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            } else {
                propValEnumPanel.setVisible(false);
                propParamPanel.setVisible(false);
            }
        } else if (ACTION_ADD_PROP_VALENUM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int position=((CTCapPropValEnumTableModel)
                            ctCapPropValEnumTable.getModel()).addVal();
            ctCapPropValEnumTable.requestFocus();
            ctCapPropValEnumTable.getSelectionModel()
                                 .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=ctCapPropValEnumTable.getRowHeight()*(position+1);
            ctCapPropValEnumTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_PROP_VALENUM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int selectedIndex = ctCapPropValEnumTable.getSelectedRow();
            ((CTCapPropValEnumTableModel) ctCapPropValEnumTable.getModel())
                                                     .remVal(selectedIndex);
            if (ctCapPropValEnumTable.getRowCount() >0){
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                ctCapPropValEnumTable
                        .setRowSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*ctCapPropValEnumTable.getRowHeight();
                ctCapPropValEnumTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }

        } else if (ACTION_ADD_PROP_PARAM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int position=((CTCapPropParamTableModel)
                            ctCapPropParamsTable.getModel()).addPropParam();
            ctCapPropParamsTable.requestFocus();
            ctCapPropParamsTable.getSelectionModel()
                                .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=ctCapPropParamsTable.getRowHeight()*(position+1);
            ctCapPropParamsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_PROP_PARAM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int selectedIndex = ctCapPropParamsTable.getSelectedRow();
            ((CTCapPropParamTableModel) ctCapPropParamsTable.getModel())
                                                   .remPropParam(selectedIndex);
            if (ctCapPropParamsTable.getModel().getRowCount() <= 0) {
                //
                // If there is no line into ctCapPropParamsTable and it is the
                // first time that the panel of CTCap is loaded, then the
                // paramValEnumPanel is null
                //
                if (paramValEnumPanel != null) {
                    paramValEnumPanel.setVisible(false);
                }
            } else {
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                ctCapPropParamsTable.getSelectionModel()
                            .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*ctCapPropParamsTable.getRowHeight();
                ctCapPropParamsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_PROP_PARAM_VALENUM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int position=((CTCapPropParamValEnumTableModel)
                           ctCapPropParamValEnumTable.getModel()).addVal();
            ctCapPropParamValEnumTable.requestFocus();
            ctCapPropParamValEnumTable.getSelectionModel()
                                      .addSelectionInterval(position, position);
             //scroll to the new added row
            int rowPoz=ctCapPropParamValEnumTable.getRowHeight()*(position+1);
            ctCapPropParamValEnumTable.scrollRectToVisible(
                    new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_PROP_PARAM_VALENUM.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            int selectedIndex = ctCapPropParamValEnumTable.getSelectedRow();
            ((CTCapPropParamValEnumTableModel) ctCapPropParamValEnumTable
                                    .getModel()).remVal(selectedIndex);
            if (ctCapPropParamValEnumTable.getRowCount() >0){
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                ctCapPropParamValEnumTable
                        .setRowSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*ctCapPropParamValEnumTable.getRowHeight();
                ctCapPropParamValEnumTable.scrollRectToVisible(
                        new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_SAVE.equals(e.getActionCommand())) {
            //stop editing on all tables
            stopTablesEditing();
            this.setVisible(false);
        } else if (ACTION_DISCARD.equals(e.getActionCommand())) {
            stopTablesEditing();
            //confirmation message
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES),
                    Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == 0) {
                //roll back to original properties
                this.ctCap.setProperties(originalProps);
                this.setVisible(false);
            }
        }
    }

    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened(WindowEvent e) {}

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.
     */
    public void windowClosing(WindowEvent e) {
        //stop tables edditing
        /*
        stopTablesEditing();
        //ask if save or not
        int answer = JOptionPane.showConfirmDialog(
                null, Bundle.getMessage(Bundle.MSG_SAVE_CHANGES),
                Bundle.getMessage(Bundle.MSG_SAVE_CHANGES_TITLE),
                JOptionPane.YES_NO_OPTION);
        if (answer == 1) {
            //roll back to original properties
            this.ctCap.setProperties(originalProps);
        }
        */
    }

    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     */
    public void windowClosed(WindowEvent e) {}

    /**
     * Invoked when a window is changed from a normal to a
     * minimized state.
     */
    public void windowIconified(WindowEvent e) {}

    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     */
    public void windowDeiconified(WindowEvent e) {}

    /**
     * Invoked when the Window is set to be the active Window.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Invoked when a Window is no longer the active Window.
     */
    public void windowDeactivated(WindowEvent e) {}

    // --------------------------------------------------------- Private Methods
    /**
     * Build the interface of the panel
     */
    private void buildInterface() {
        this.getContentPane().removeAll();
        JPanel layer = new JPanel();
        layer.setLayout(null);

        JLabel labelCTCapProp = new JLabel(
                Bundle.getMessage(Bundle.LABEL_CTCAP_PROPERTIES));
        labelCTCapProp.setFont(GuiFactory.titlePanelFont);
        labelCTCapProp.setBounds(10, 10, 220, 20);
        labelCTCapProp.setBorder(new TitledBorder(""));

        layer.add(labelCTCapProp);

        JLabel labelCTCapType = new JLabel(
                Bundle.getMessage(Bundle.LABEL_TYPE) + "    " +
                ctCap.getCTInfo().getCTType()
                );
        labelCTCapType.setFont(GuiFactory.defaultFont);
        labelCTCapType.setBounds(10, 35, 220, 20);
        layer.add(labelCTCapType);

        JLabel labelCTCapVersion = new JLabel(
                Bundle.getMessage(Bundle.LABEL_VERSION) + "  " +
                ctCap.getCTInfo().getVerCT()
                );
        labelCTCapVersion.setFont(GuiFactory.defaultFont);
        labelCTCapVersion.setBounds(10, 55, 220, 20);
        layer.add(labelCTCapVersion);

        JLabel labelProperties = new JLabel(
                Bundle.getMessage(Bundle.LABEL_PROPERTIES)
                );
        labelProperties.setFont(GuiFactory.defaultTableHeaderFont);
        labelProperties.setBounds(10, 90, 220, 20);
        layer.add(labelProperties);

        CTCapPropertiesTableModel propTM = new CTCapPropertiesTableModel(ctCap);
        ctCapPropsTable = new JTable(propTM);
        //ctCap properties add/remove buttons
        JButton addCTCapProp = new JButton("+");
        addCTCapProp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0))
                );
        addCTCapProp.setMargin(new Insets(0, 0, 0, 0));
        addCTCapProp.setBounds(520, 90, 18, 18);
        addCTCapProp.setActionCommand(ACTION_ADD_PROP);
        addCTCapProp.addActionListener(this);
        //layer.add(addCTCapProp);
        
        JButton remCTCapProp = new JButton("-");
        remCTCapProp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0))
                );
        remCTCapProp.setMargin(new Insets(0, 0, 0, 0));
        remCTCapProp.setBounds(540, 90, 18, 18);
        remCTCapProp.setActionCommand(ACTION_REM_PROP);
        remCTCapProp.addActionListener(this);
        //layer.add(remCTCapProp);
        
        //code for selection listener
        ctCapPropsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel ctCapPropSelectionModel = ctCapPropsTable.getSelectionModel();

        ctCapPropSelectionModel.addListSelectionListener(
           new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                //stop editing on all tables
                stopTablesEditing();

                if (!lsm.isSelectionEmpty()) {
                    if (lsm.getMinSelectionIndex() < ctCap.getProperties().size()) {
                        curentProp = (Property) ctCap.getProperties()
                                        .get(lsm.getMinSelectionIndex());
                        if (propValEnumPanel == null) {
                            builPropValEnum(lsm.getMinSelectionIndex());
                        }
                        if (propParamPanel == null) {
                            buildPropParam(lsm.getMinSelectionIndex());
                        }
                        if (!propParamPanel.isVisible()) {
                            propValEnumPanel.setVisible(true);
                            propParamPanel.setVisible(true);
                        }
                        ctCapPropValEnumTable.setModel(
                                new CTCapPropValEnumTableModel(
                                    (Property) ctCap.getProperties()
                                        .get(lsm.getMinSelectionIndex()))
                                 );
                        ctCapPropParamsTable.setModel(
                                new CTCapPropParamTableModel(
                                    (Property) ctCap.getProperties()
                                        .get(lsm.getMinSelectionIndex()))
                                 );

                        if(propParamPanel.isVisible()){
                            if( (ctCapPropParamsTable!=null)
                                &&(ctCapPropParamsTable.getSelectionModel()!=null)
                                &&(ctCapPropParamsTable.getSelectionModel().isSelectionEmpty())
                                &&(paramValEnumPanel != null)
                                &&(paramValEnumPanel.isVisible())) {
                               paramValEnumPanel.setVisible(false);
                            }
                            if((ctCapPropParamsTable==null)||
                               (ctCapPropParamsTable.getSelectionModel()==null)) {
                                paramValEnumPanel.setVisible(false);
                            }
                        }
                    }
                }
            }
        });

        TableColumn columnNoTruncate = ctCapPropsTable.getColumnModel().getColumn(5);
        columnNoTruncate.setCellEditor(new DefaultCellEditor(
                new JComboBox(CapabilitiesPanel.BOOLEAN_VALUES)));

        JScrollPane propScrollPane = new JScrollPane(ctCapPropsTable);
        propScrollPane.setBounds(10, 110, 550, 150);

        layer.add(propScrollPane);

        //select first item in the ctcaps properties list
        ctCapPropsTable.getSelectionModel().addSelectionInterval(0, 0);

        //save button
        JButton saveButton = new JButton(Bundle.getMessage(Bundle.BUTTON_OK));
        saveButton.setBounds(347, 470, 80, 30);
        saveButton.setActionCommand(ACTION_SAVE);
        saveButton.addActionListener(this);
        layer.add(saveButton);
        //discard button
        JButton discardButton = new JButton(
                Bundle.getMessage(Bundle.BUTTON_DISCARD));
        discardButton.setBounds(405, 470, 80, 30);
        discardButton.setActionCommand(ACTION_DISCARD);
        discardButton.addActionListener(this);
        //layer.add(discardButton);

        this.getContentPane().add(layer);
    }

    /**
     * Builds the table with val enums for a ctcap property
     *
     * @param index the index of the ctcap property
     */
    private void builPropValEnum(int index) {
        propValEnumPanel = new JPanel();
        propValEnumPanel.setLayout(null);
        propValEnumPanel.setBounds(590, 90, 170, 170);

        JLabel labelPropValEnum = new JLabel(
                Bundle.getMessage(Bundle.LABEL_VAL_ENUM));
        labelPropValEnum.setFont(GuiFactory.defaultTableHeaderFont);
        labelPropValEnum.setBounds(0, 0, 170, 20);

        propValEnumPanel.add(labelPropValEnum, 0);

        //properties val enum table
        CTCapPropValEnumTableModel propValEnumTM
                = new CTCapPropValEnumTableModel(
                    (Property) ctCap.getProperties().get(index)
                  );
        ctCapPropValEnumTable = new JTable(propValEnumTM);
        ctCapPropValEnumTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //ctCap properties val enum  add/remove buttns
        JButton addCTCapPropValEnum = new JButton("+");
        addCTCapPropValEnum.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0)));
        addCTCapPropValEnum.setMargin(new Insets(0, 0, 0, 0));
        addCTCapPropValEnum.setBounds(130, 0, 18, 18);
        addCTCapPropValEnum.setActionCommand(ACTION_ADD_PROP_VALENUM);
        addCTCapPropValEnum.addActionListener(this);
        //propValEnumPanel.add(addCTCapPropValEnum);

        JButton remCTCapPropValEnum = new JButton("-");
        remCTCapPropValEnum.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        remCTCapPropValEnum.setMargin(new Insets(0, 0, 0, 0));
        remCTCapPropValEnum.setBounds(150, 0, 18, 18);
        remCTCapPropValEnum.setActionCommand(ACTION_REM_PROP_VALENUM);
        remCTCapPropValEnum.addActionListener(this);
        //propValEnumPanel.add(remCTCapPropValEnum);

        JScrollPane propValEnumScrollPane = new JScrollPane(ctCapPropValEnumTable);
        propValEnumScrollPane.setBounds(0, 20, 170, 150);
        propValEnumPanel.add(propValEnumScrollPane);

        this.getContentPane().add(propValEnumPanel,0);
        this.getContentPane().repaint();
    }

    /**
     * Builds the interface for ctcap param properties
     *
     * @param index the index of the ctcap property
     */
    private void buildPropParam(int index) {
        propParamPanel = new JPanel();
        propParamPanel.setLayout(null);
        propParamPanel.setBounds(10, 280, 550, 170);

        //ctcap properties param
        JLabel labelParameters = new JLabel(
                Bundle.getMessage(Bundle.LABEL_PARAMETERS));
        labelParameters.setFont(GuiFactory.defaultTableHeaderFont);
        labelParameters.setBounds(0, 0, 220, 20);
        propParamPanel.add(labelParameters);

        CTCapPropParamTableModel propParamTM
                = new CTCapPropParamTableModel(
                        (Property) ctCap.getProperties().get(index)
                      );
        ctCapPropParamsTable = new JTable(propParamTM);
        ctCapPropParamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //ctCap property params add/remove buttons
        JButton addCTCapPropParam = new JButton("+");
        addCTCapPropParam.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0))
                );
        addCTCapPropParam.setMargin(new Insets(0, 0, 0, 0));
        addCTCapPropParam.setBounds(510, 0, 18, 18);
        addCTCapPropParam.setActionCommand(ACTION_ADD_PROP_PARAM);
        addCTCapPropParam.addActionListener(this);
        //propParamPanel.add(addCTCapPropParam);

        JButton remCTCapPropParam = new JButton("-");
        remCTCapPropParam.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0))
                );
        remCTCapPropParam.setMargin(new Insets(0, 0, 0, 0));
        remCTCapPropParam.setBounds(530, 0, 18, 18);
        remCTCapPropParam.setActionCommand(ACTION_REM_PROP_PARAM);
        remCTCapPropParam.addActionListener(this);
        //propParamPanel.add(remCTCapPropParam);

        //code for selection listener
        ctCapPropParamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel propParamSelectionModel = ctCapPropParamsTable
                        .getSelectionModel();

        propParamSelectionModel.addListSelectionListener(
          new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                //stop editing on all tables
                stopTablesEditing();

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    if (lsm.getMinSelectionIndex() <
                            curentProp.getPropParams().size()) {
                        if ((paramValEnumPanel == null)
                            || (!paramValEnumPanel.isVisible())) {
                            buildPropParamValEnum(
                                    lsm.getMinSelectionIndex());
                        }
                        ctCapPropParamValEnumTable.setModel(
                                new CTCapPropParamValEnumTableModel(
                                      (PropParam) curentProp.getPropParams()
                                         .get(lsm.getMinSelectionIndex()))
                                    );
                    }
                }
            }
        });

        //end code for selection listener
        JScrollPane propParamScrollPane = new JScrollPane(ctCapPropParamsTable);
        propParamScrollPane.setBounds(0, 20, 550, 150);



        propParamPanel.add(propParamScrollPane);

        this.getContentPane().add(propParamPanel,0);
        this.getContentPane().repaint();
    }

    /**
     * Build the table for ctcaps property param valenum
     *
     * @param index
     */
    private void buildPropParamValEnum(int index) {
        paramValEnumPanel = new JPanel();
        paramValEnumPanel.setLayout(null);
        paramValEnumPanel.setBounds(590, 280, 170, 170);

        JLabel labelPropParamValEnum = new JLabel(
                Bundle.getMessage(Bundle.LABEL_VAL_ENUM));
        labelPropParamValEnum.setFont(GuiFactory.defaultTableHeaderFont);
        labelPropParamValEnum.setBounds(0, 0, 170, 20);
        paramValEnumPanel.add(labelPropParamValEnum);

        //val enum table
        PropParam tempParam = (PropParam) curentProp.getPropParams().get(index);
        CTCapPropParamValEnumTableModel propParamValEnumTM
                = new CTCapPropParamValEnumTableModel(tempParam);
        ctCapPropParamValEnumTable = new JTable(propParamValEnumTM);
        ctCapPropParamValEnumTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        //ctCap properties val enum  add/remove buttons
        JButton addCTCapPropParamValEnum = new JButton("+");
        addCTCapPropParamValEnum.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(0,0,0,0)));
        addCTCapPropParamValEnum.setMargin(new Insets(0, 0, 0, 0));
        addCTCapPropParamValEnum.setBounds(130, 0, 18, 18);
        addCTCapPropParamValEnum.setActionCommand(ACTION_ADD_PROP_PARAM_VALENUM);
        addCTCapPropParamValEnum.addActionListener(this);
        //paramValEnumPanel.add(addCTCapPropParamValEnum);

        JButton remCTCapPropParamValEnum = new JButton("-");
        remCTCapPropParamValEnum.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        remCTCapPropParamValEnum.setMargin(new Insets(0, 0, 0, 0));
        remCTCapPropParamValEnum.setBounds(150, 0, 18, 18);
        remCTCapPropParamValEnum.setActionCommand(ACTION_REM_PROP_PARAM_VALENUM);
        remCTCapPropParamValEnum.addActionListener(this);
        //paramValEnumPanel.add(remCTCapPropParamValEnum);

        JScrollPane propParamValEnumScrollPane
                = new JScrollPane(ctCapPropParamValEnumTable);
        propParamValEnumScrollPane.setBounds(0, 20, 170, 150);
        paramValEnumPanel.add(propParamValEnumScrollPane);

        this.getContentPane().add(paramValEnumPanel,0);
        this.getContentPane().repaint();
    }

    /**
     *  When this method is called all the tables in panel stop editting
     **/
    private void stopTablesEditing() {
        if ((this.ctCapPropsTable != null)&&
                (this.ctCapPropsTable.isEditing())) {
            this.ctCapPropsTable.getCellEditor().stopCellEditing();
        }
        if ((this.ctCapPropValEnumTable!=null)&&
                (this.ctCapPropValEnumTable.isEditing())) {
            this.ctCapPropValEnumTable.getCellEditor().stopCellEditing();
        }
        if ((this.ctCapPropParamsTable!=null)&&
                (this.ctCapPropParamsTable.isEditing())) {
            this.ctCapPropParamsTable.getCellEditor().stopCellEditing();
        }
        if ((this.ctCapPropParamValEnumTable!=null)&&
                (this.ctCapPropParamValEnumTable.isEditing())) {
            this.ctCapPropParamValEnumTable.getCellEditor().stopCellEditing();
        }
    }
}
