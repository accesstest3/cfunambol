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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.openide.windows.WindowManager;

import com.funambol.framework.core.FilterCap;
import com.funambol.admin.ui.GuiFactory;

/**
 * Dialog with the properties of a filterCap.
 *
 * @version $Id: FilterCapDialog.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class FilterCapDialog extends JDialog implements ActionListener, WindowListener {

    // --------------------------------------------------------------- Constants
    private static final String ACTION_SAVE         = "ACTION_SAVE";
    private static final String ACTION_DISCARD      = "ACTION_DISCARD";
    private static final String ACTION_ADD_KEYWORD  = "ACTION_ADD_KEYWORD";
    private static final String ACTION_REM_KEYWORD  = "ACTION_REM_KEYWORD";
    private static final String ACTION_ADD_PROPERTY = "ACTION_ADD_PROPERTY";
    private static final String ACTION_REM_PROPERTY = "ACTION_REM_PROPERTY";

    // ------------------------------------------------------------ Private data
    /**
     * the object displayed by the panel
     */
    private FilterCap filterCap = null;
    private ArrayList originalKeywords  = null;
    private ArrayList originalPropNames = null;
    //components on panel
    private JTable keywordsTable = null;
    private JTable propertiesTable = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Constructor for FilterCapDialog which is a JDialog. This creates a modal
     * or non-modal dialog without a title and with the specified owner frame
     *
     * @param frame is the owner frame of the dialog
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     */
    private FilterCapDialog(Frame frame, boolean modal) {
        super(WindowManager.getDefault().getMainWindow(),modal);
        this.setResizable(false);
        this.setBounds(0, 0, 370, 350);
        this.setTitle(Bundle.getMessage(
                        Bundle.LABEL_FILTER_CAP_PROPERTIES));
        this.addWindowListener(this);
        this.setLocationRelativeTo(this.getParent());
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Creates a dialog for editing the properties of a FilterCap
     *
     * @param table the table from where the dialog is opened
     * @param modal a boolean. When true, the remainder of the program is
     *              inactive until the dialog is closed.
     *
     * @return new instance of FilterCapDialog
     */
    public static FilterCapDialog createDialog(JButton table, boolean modal) {
        return new FilterCapDialog(
                JOptionPane.getFrameForComponent(table), modal);
    }

    /**
     * Sets the object that is displayed for editing on the dialog
     *
     * @param filterCap the object to be edited
     */
    public void setFilterCap(FilterCap filterCap) {
        if(filterCap != null) {
            this.filterCap = filterCap;
            //clone the initial arrays for Keywords and PropNames
            originalKeywords = new ArrayList();
            for(int i=0;i<filterCap.getFilterKeywords().size();i++) {
                originalKeywords.add(new String(
                        (String)filterCap.getFilterKeywords().get(i)));
            }
            originalPropNames = new ArrayList();
            for(int i=0;i<filterCap.getPropNames().size();i++) {
                originalPropNames.add(new String(
                        (String)filterCap.getPropNames().get(i)));
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
            stopTablesEditing();
            this.setVisible(false);
        }else if (ACTION_DISCARD.equals(e.getActionCommand())) {
            stopTablesEditing();
            //confirmation message
            int answer = JOptionPane.showConfirmDialog(
                    null, Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES),
                    Bundle.getMessage(Bundle.MSG_DISCARD_CHANGES_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == 0) {
                //roll back to original keywords and propNames
                this.filterCap.setFilterKeywords(originalKeywords);
                this.filterCap.setPropNames(originalPropNames);
                this.setVisible(false);
            }
        } else if (ACTION_ADD_KEYWORD.equals(e.getActionCommand())) {
            stopTablesEditing();
            int position = ((FilterCapKeywordsTableModel) keywordsTable.
                    getModel()).addKeyword();
            keywordsTable.requestFocus();
            keywordsTable.getSelectionModel()
                         .addSelectionInterval(position ,position);
            //scroll to the new added row
            int rowPoz=keywordsTable.getRowHeight()*(position+1);
            keywordsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_KEYWORD.equals(e.getActionCommand())) {
            stopTablesEditing();
            int selectedIndex = keywordsTable.getSelectedRow();
            ((FilterCapKeywordsTableModel) keywordsTable.getModel())
                    .remKeyword(selectedIndex);
            if (keywordsTable.getModel().getRowCount() > 0) {
                keywordsTable.requestFocus();
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                keywordsTable.getSelectionModel()
                             .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*keywordsTable.getRowHeight();
                keywordsTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
            }
        } else if (ACTION_ADD_PROPERTY.equals(e.getActionCommand())) {
            stopTablesEditing();
            int position = ((FilterCapPropertiesTableModel) propertiesTable.
                    getModel()).addProperty();
            propertiesTable.requestFocus();
            propertiesTable.getSelectionModel()
                           .addSelectionInterval(position, position);
            //scroll to the new added row
            int rowPoz=propertiesTable.getRowHeight()*(position+1);
            propertiesTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
        } else if (ACTION_REM_PROPERTY.equals(e.getActionCommand())) {
            stopTablesEditing();
            int selectedIndex = propertiesTable.getSelectedRow();
            ((FilterCapPropertiesTableModel) propertiesTable.getModel())
                            .remProperty(selectedIndex);
            if (propertiesTable.getModel().getRowCount() > 0) {
                propertiesTable.requestFocus();
                //select row with index selectedIndex-1 and scroll to that row
                selectedIndex=selectedIndex-1;
                if(selectedIndex<0) {
                    selectedIndex=0;
                }
                propertiesTable.getSelectionModel()
                            .addSelectionInterval(selectedIndex, selectedIndex);
                int rowPoz=(selectedIndex+1)*propertiesTable.getRowHeight();
                propertiesTable.scrollRectToVisible(new Rectangle(0,rowPoz,1,1));
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
        /*
        //stop tables edditing
        stopTablesEditing();
        //ask if save or not
        int answer = JOptionPane.showConfirmDialog(
                null, Bundle.getMessage(Bundle.MSG_SAVE_CHANGES),
                Bundle.getMessage(Bundle.MSG_SAVE_CHANGES_TITLE),
                JOptionPane.YES_NO_OPTION);
        if (answer == 1) {
            this.filterCap.setFilterKeywords(originalKeywords);
            this.filterCap.setPropNames(originalPropNames);
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

        JLabel labelFilterCapProp = new JLabel(
                Bundle.getMessage(
                        Bundle.LABEL_FILTER_CAP_PROPERTIES));
        labelFilterCapProp.setFont(GuiFactory.titlePanelFont);
        labelFilterCapProp.setBounds(10, 10, 220, 20);
        labelFilterCapProp.setBorder(new TitledBorder(""));
        layer.add(labelFilterCapProp);

        JLabel labelFilterCapType = new JLabel(
                Bundle.getMessage(Bundle.LABEL_TYPE) + "    " +
                                        filterCap.getCTInfo().getCTType());
        labelFilterCapType.setFont(GuiFactory.defaultFont);
        labelFilterCapType.setBounds(10, 35, 220, 20);
        layer.add(labelFilterCapType);

        JLabel labelFilterCapVersion = new JLabel(
                Bundle.getMessage(Bundle.LABEL_VERSION) + "  " +
                                        filterCap.getCTInfo().getVerCT());
        labelFilterCapVersion.setFont(GuiFactory.defaultFont);
        labelFilterCapVersion.setBounds(10, 55, 220, 20);
        layer.add(labelFilterCapVersion);


        JLabel labelKeywords = new JLabel(
                Bundle.getMessage(Bundle.LABEL_KEYWORDS));
        labelKeywords.setFont(GuiFactory.defaultTableHeaderFont);
        labelKeywords.setBounds(10, 90, 220, 20);
        layer.add(labelKeywords);

        FilterCapKeywordsTableModel keywordsTM
                     = new FilterCapKeywordsTableModel(filterCap);
        keywordsTable = new JTable(keywordsTM);
        keywordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //filtercap keywords add/remove buttons
        JButton addKeyword = new JButton("+");
        addKeyword.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        addKeyword.setMargin(new Insets(0, 0, 0, 0));
        addKeyword.setBounds(120, 90, 18, 18);
        addKeyword.setActionCommand(ACTION_ADD_KEYWORD);
        addKeyword.addActionListener(this);
        //layer.add(addKeyword);

        JButton remKeyword = new JButton("-");
        remKeyword.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        remKeyword.setMargin(new Insets(0, 0, 0, 0));
        remKeyword.setBounds(140, 90, 18, 18);
        remKeyword.setActionCommand(ACTION_REM_KEYWORD);
        remKeyword.addActionListener(this);
        //layer.add(remKeyword);

        JScrollPane keywordsScrollPane = new JScrollPane(keywordsTable);
        keywordsScrollPane.setBounds(10, 110, 150, 150);
        layer.add(keywordsScrollPane);

        JLabel labelProperties = new JLabel(
                Bundle.getMessage(Bundle.LABEL_PROPERTIES));
        labelProperties.setFont(GuiFactory.defaultTableHeaderFont);
        labelProperties.setBounds(200, 90, 220, 20);
        layer.add(labelProperties);

        FilterCapPropertiesTableModel peropertiesTM
                = new FilterCapPropertiesTableModel(filterCap);
        propertiesTable = new JTable(peropertiesTM);
        propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //filtercap properties add/remove buttons
        JButton addProperty = new JButton("+");
        addProperty.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(
                                0, 0, 0, 0)));
        addProperty.setMargin(new Insets(0, 0, 0, 0));
        addProperty.setBounds(310, 90, 18, 18);
        addProperty.setActionCommand(ACTION_ADD_PROPERTY);
        addProperty.addActionListener(this);
        //layer.add(addProperty);

        JButton remProperty = new JButton("-");
        remProperty.setFont(GuiFactory.defaultFont);
        remProperty.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        remProperty.setMargin(new Insets(0, 0, 0, 0));
        remProperty.setBounds(330, 90, 18, 18);
        remProperty.setActionCommand(ACTION_REM_PROPERTY);
        remProperty.addActionListener(this);
        //layer.add(remProperty);

        JScrollPane propertiesScrollPane = new JScrollPane(propertiesTable);
        propertiesScrollPane.setBounds(200, 110, 150, 150);
        layer.add(propertiesScrollPane);

        //save button
        JButton saveButton = new JButton(
                Bundle.getMessage(Bundle.BUTTON_OK));
        saveButton.setBounds(140, 280, 80, 30);
        saveButton.setActionCommand(ACTION_SAVE);
        saveButton.addActionListener(this);
        layer.add(saveButton);
        //discard button
        JButton discardButton = new JButton(
                Bundle.getMessage(Bundle.BUTTON_DISCARD));
        discardButton.setBounds(185, 280, 80, 30);
        discardButton.setActionCommand(ACTION_DISCARD);
        discardButton.addActionListener(this);
        //layer.add(discardButton);

        this.getContentPane().add(layer);
    }

    /**
     *  When this method is called all the tables in panel stop editting
     **/
    private void stopTablesEditing() {
        if((keywordsTable!=null)&&
                (keywordsTable.isEditing())) {
            keywordsTable.getCellEditor().stopCellEditing();
        }
        if((propertiesTable!=null)&&
                (propertiesTable.isEditing())) {
            propertiesTable.getCellEditor().stopCellEditing();
        }
    }
}
