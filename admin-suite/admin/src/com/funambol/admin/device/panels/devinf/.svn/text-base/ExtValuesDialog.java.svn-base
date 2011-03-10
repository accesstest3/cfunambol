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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;

import org.openide.windows.WindowManager;

import com.funambol.framework.core.Ext;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;

/**
 * The dialog with the properties of an Ext.
 *
 * @version $Id: ExtValuesDialog.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class ExtValuesDialog extends JDialog implements ActionListener, WindowListener {

    // --------------------------------------------------------------- Constants
    private static final String ACTION_SAVE         = "ACTION_SAVE";
    private static final String ACTION_DISCARD      = "ACTION_DISCARD";
    private static final String ACTION_ADD_XVALUE   = "ACTION_ADD_XVALUE";
    private static final String ACTION_REM_XVALUE   = "ACTION_REM_XVALUE";

    // ------------------------------------------------------------ Private data
    /**
     * the Ext object that is displayed by this dialog
     */
    private Ext ext = null;
    private ArrayList originalXValues;
    //components on panel
    private JTable xValuesTable = null;
    private JButton saveButton = new JButton(
            Bundle.getMessage(Bundle.BUTTON_OK));
    private JButton discardButton = new JButton(
            Bundle.getMessage(Bundle.BUTTON_DISCARD));

    // ------------------------------------------------------------- Constructor

    /**
     * Creates an empty ExtValuesDialog but it doesn't show it. This constructor
     * is private because dialogs must be created using createDialog() static
     * method of this class.
     *
     * @param frame parent frame
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     */
    private ExtValuesDialog(Frame frame, boolean modal) {
        super(WindowManager.getDefault().getMainWindow(),modal);
        this.setResizable(false);
        this.setBounds(0, 0, 205, 350);
        this.setTitle(Bundle.getMessage(Bundle.LABEL_EXT_PROPERTIES));
        this.addWindowListener(this);
        this.setLocationRelativeTo(this.getParent());
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sets the object that is displayed for editing on the dialog
     *
     * @param ext the object to be edited
     */
    public void setExt(Ext ext) {
        if(ext!=null) {
            this.ext = ext;
            //clone the initial XValues array
            originalXValues = new ArrayList();
            for(int i=0;i<ext.getXVal().size();i++) {
                originalXValues.add(new String((String)ext.getXVal().get(i)));
            }
            buildInterface();
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        if (ACTION_SAVE.equals(e.getActionCommand())) {
            if((xValuesTable!=null)&&
                    (xValuesTable.isEditing())) {
                xValuesTable.getCellEditor().stopCellEditing();
            }
            this.setVisible(false);
        } else if (ACTION_DISCARD.equals(e.getActionCommand())) {
            if((xValuesTable!=null)&&
                    (xValuesTable.isEditing())) {
                xValuesTable.getCellEditor().stopCellEditing();
            }
            //confirmation message
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES),
                    Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == 0) {
                //roll back to original XValues
                this.ext.setXVal(originalXValues);
            this.setVisible(false);
            }
        } else if (ACTION_ADD_XVALUE.equals(e.getActionCommand())) {
            if((xValuesTable!=null)&&
                    (xValuesTable.isEditing())) {
                xValuesTable.getCellEditor().stopCellEditing();
            }
            int position = ((ExtValuesTableModel) xValuesTable.getModel()).addValue();
            xValuesTable.requestFocus();
            xValuesTable.getSelectionModel()
                        .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=xValuesTable.getRowHeight()*(position+1);
            xValuesTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_XVALUE.equals(e.getActionCommand())) {
            if((xValuesTable!=null)&&
                    (xValuesTable.isEditing())) {
                xValuesTable.getCellEditor().stopCellEditing();
            }
            int selectedIndex = xValuesTable.getSelectedRow();
            ((ExtValuesTableModel) xValuesTable.getModel()).remValue(
                    selectedIndex);
            if (xValuesTable.getModel().getRowCount() > 0) {
                xValuesTable.requestFocus();
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                xValuesTable.getSelectionModel()
                            .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*xValuesTable.getRowHeight();
                xValuesTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        }
    }

    /**
     * Creates a dialog for editing the xValues of an Ext
     *
     * @param table the table from where the dialog is opened
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     *
     * @return new instance of ExtValuesDialog
     */
    public static ExtValuesDialog createDialog(JButton table, boolean modal) {
        return new ExtValuesDialog(
                JOptionPane.getFrameForComponent(table.getParent()), modal);
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
        if((xValuesTable!=null)&&
                (xValuesTable.isEditing())) {
            xValuesTable.getCellEditor().stopCellEditing();
        }
        //ask if save or not
        int answer = JOptionPane.showConfirmDialog(
                null, Bundle.getMessage(Bundle.MSG_SAVE_CHANGES),
                Bundle.getMessage(Bundle.MSG_SAVE_CHANGES_TITLE),
                JOptionPane.YES_NO_OPTION);
        if (answer == 1) {
            this.ext.setXVal(originalXValues);
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

        JLabel labelExtProp = new JLabel(
                Bundle.getMessage(Bundle.LABEL_EXT_PROPERTIES));
        labelExtProp.setFont(GuiFactory.titlePanelFont);
        labelExtProp.setBounds(10, 10, 180, 20);
        labelExtProp.setBorder(new TitledBorder(""));
        layer.add(labelExtProp);

        JLabel labelExtName = new JLabel(
                Bundle.getMessage(Bundle.LABEL_EXT_NAME) + "    " +
                                        ext.getXNam());
        labelExtName.setFont(GuiFactory.defaultFont);
        labelExtName.setBounds(10, 35, 200, 20);
        layer.add(labelExtName);

        JLabel labelXValues = new JLabel(
                Bundle.getMessage(Bundle.LABEL_KEYWORDS));
        labelXValues.setFont(GuiFactory.defaultTableHeaderFont);
        labelXValues.setBounds(10, 80, 200, 20);
        layer.add(labelXValues);

        ExtValuesTableModel extValuesTM = new ExtValuesTableModel(ext);
        xValuesTable = new JTable(extValuesTM);
        xValuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //ext values  add/remove buttons
        JButton addXValue = new JButton("+");
        addXValue.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        addXValue.setMargin(new Insets(0, 0, 0, 0));
        addXValue.setBounds(145, 80, 18, 18);
        addXValue.setActionCommand(ACTION_ADD_XVALUE);
        addXValue.addActionListener(this);
        //layer.add(addXValue);

        JButton remXValue = new JButton("-");
        remXValue.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        remXValue.setMargin(new Insets(0, 0, 0, 0));
        remXValue.setBounds(165, 80, 18, 18);
        remXValue.setActionCommand(ACTION_REM_XVALUE);
        remXValue.addActionListener(this);
        //layer.add(remXValue);

        JScrollPane extValScrollPane = new JScrollPane(xValuesTable);
        extValScrollPane.setBounds(10, 100, 175, 150);
        layer.add(extValScrollPane);

        //save button
        saveButton.setBounds(57, 275, 80, 30);
        saveButton.setActionCommand(ACTION_SAVE);
        saveButton.addActionListener(this);
        layer.add(saveButton);
        //discard button
        discardButton.setBounds(100, 275, 80, 30);
        discardButton.setActionCommand(ACTION_DISCARD);
        discardButton.addActionListener(this);
        //layer.add(discardButton);

        this.getContentPane().add(layer);
    }
}
