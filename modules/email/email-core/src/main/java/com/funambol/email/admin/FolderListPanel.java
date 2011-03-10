/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.email.admin;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServerAccount;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;

/**
 * Admin panel used to map imap mail account folders with folders to be 
 * synchronized for a mail server account.
 *
 * @version $Id: FolderListPanel.java,v 1.1 2008-03-25 11:28:17 gbmiglia Exp $
 */
public class FolderListPanel extends JPanel {

    // ------------------------------------------------------------ private data

    /** Selectable folder names. */
    private final static String[] DESTINATION_FOLDER_NAMES =
    {"", "Inbox", "Outbox", "Sent", "Drafts", "Trash"};

    /** Visual elements. */
    private JButton jBCancel = new JButton();
    private JButton jBSave   = new JButton();
    private JTable table;

    /** Data model. */
    private String[] foldersNames;
    private AccountFolderTableModel tableModel = new AccountFolderTableModel();

    /** Mapping between account folder names and folder to be synchronized. */
    private Map mapping;

    /** Mail server account. */
    private MailServerAccount msa;

    // ------------------------------------------------------------ constructors

    /**
     * Creates a new instance of SearchAccountFolder.
     *
     * @param foldersNames names of the folders for given mail server account
     * @param msa imap mail server account
     */
    public FolderListPanel(String[] foldersNames, MailServerAccount msa) {

        this.msa = msa;

        // init model
        initModel(foldersNames);

        // init visual component
        initGui();
    }

    // --------------------------------------------------------- private methods

    /**
     * Initializes the visual elements.
     */
    private void initGui(){

        //
        // Panel layout
        //
        setLayout(null);

        //
        // Configure panel components
        //

        // title

        JLabel panelName = new JLabel();
        panelName.setFont(GuiFactory.titlePanelFont);

        panelName.setText("Account folders");

        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(new TitledBorder(""));

        // folders names table

        table = new JTable(tableModel);
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(400, 120));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        tableScrollPane.setBounds(new Rectangle(0, 40, 400, 200));

        final TableColumn col = table.getColumnModel().getColumn(1);
        col.setCellEditor(new ComboBoxEditor(DESTINATION_FOLDER_NAMES));
        col.setCellRenderer(new ComboBoxRenderer(DESTINATION_FOLDER_NAMES));

        //
        // Every time a destination folder is selected the following task have
        // to be performed:
        //
        // - checks if already exists a mapping for the selected destination folder;
        //   in that case a suitable message should be shown
        // - updates current mapping
        //
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {

                int rowIndex = e.getFirstRow();

                AccountFolderTableModel mod = (AccountFolderTableModel)
                e.getSource();

                updateMapping(rowIndex);
            }
        });

        // buttons

        jBCancel.setText("Close");
        jBSave.setText("Save");

        int ypos = 300;
        int xpos = 80;
        jBSave.setBounds   (xpos,       ypos, 100, 20);
        jBCancel.setBounds (xpos + 120, ypos, 100, 20);

        //
        // Adds components to this panel.
        //

        add(panelName);

        // table

        add(tableScrollPane);

        // buttons

        add(jBCancel);
        add(jBSave);
    }

    /**
     * Initializes table model with folders names.
     *
     * @param foldersNames names of the folders for current mail server account
     */
    private void initModel(String[] foldersNames){

        //
        // Gets all folders names for this account
        //        
        this.foldersNames = foldersNames;
        
        //
        // Initializes table model with folders names.
        //
        tableModel.loadEntries(foldersNames);
        
        //
        // Initializes mapping
        //
        mapping = new HashMap();
    }

    /**
     * Updates mapping between a mail account folders name given its table row
     * index. If updated mapping already exists then a popup message is shown.
     *
     * @param rowIndex table row index
     */
    private void updateMapping(int rowIndex){

        Object[][] rowData = tableModel.getRowData();

        //
        // Checks if the mapping already exists
        //
        String destinationFolder = (String)rowData[rowIndex][1];
        if (mapping.containsKey(destinationFolder)){
            jBSave.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Mapping already present");
            return;
        }

        jBSave.setEnabled(true);

        //
        // Updates mapping.
        //
        Map newMapping = new HashMap();

        for (int i = 0; i < foldersNames.length; i++) {
            destinationFolder = (String)rowData[i][1];

            if (destinationFolder == null || destinationFolder.equals("")){
                continue;
            }

            newMapping.put(destinationFolder, rowData[i][0]);
        }
        mapping = newMapping;
    }
    
    // ----------------------------------------------------------- inner classes

    /**
     * Model of the table used to display account folders.
     */
    private class AccountFolderTableModel extends AbstractTableModel {

        /** column names */
        String[] columnNames = new String[] {
            "Name",
            "Destination"};

        /**
         * Container of data in the table.
         * <p/>
         * Must be not null at creation time.
         */
        Object[][] rowData = new Object[0][columnNames.length];

        public String getColumnName(int col) {
            return columnNames[col];
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return rowData.length;
        }
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData[rowIndex][columnIndex];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int rowIndex, int colIndex) {
            rowData[rowIndex][colIndex] = value;
            fireTableCellUpdated(rowIndex, colIndex);
        }

        /**
         * Fills table rows with given folders.
         */
        public void loadEntries(String[] foldersNames){
            int size = foldersNames.length;
            if (size != 0){
                rowData = new Object[size][columnNames.length];
                for (int i = 0; i < foldersNames.length; i++) {
                    setValueAt(foldersNames[i], i, 0);
                }
            }
        }

        public Object[][] getRowData(){
            return rowData;
        }
    }

    /**
     * It is possible to select the folder name to bound to each folder to
     * synchronize. If the empty value is selected the default value is taken.
     */
    public class ComboBoxEditor extends DefaultCellEditor {
        public ComboBoxEditor(String[] items) {
            super(new JComboBox(items));
        }
    }

    public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public ComboBoxRenderer(String[] items) {
            super(items);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }

    // ---------------------------------------------------- properties accessors

    /**
     * Gets mapping.
     */
    public Map getFolderMapping(){
        return mapping;
    }

    // ---------------------------------------------------------- public methods

    /**
     * Sets the action bound to the cancel button.
     *
     * @param actionListener action to trigger as the cancel button is pressed
     */
    public void setCancelAction(ActionListener actionListener){
        jBCancel.addActionListener(actionListener);
    }

    /**
     * Sets the action bound to the save button.
     *
     * @param actionListener action to trigger as the save button is pressed
     */
    public void setSaveAction(ActionListener actionListener){
        jBSave.addActionListener(actionListener);
    }
}
