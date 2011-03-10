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
package com.funambol.email.admin.mailservers;


import com.funambol.admin.ui.GuiFactory;
import com.funambol.email.model.MailServer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * Panel used for displaying results of a mail server search.
 *
 * @version $Id: ResultSearchMailServerPanel.java,v 1.1 2008-03-25 11:25:33 gbmiglia Exp $
 */
public class ResultSearchMailServerPanel extends JPanel{
    
    // ------------------------------------------------------------ Private data
    
    /** Mail servers currently displaied in the table. */
    private MailServer[] mailservers = null;
    
    //
    // Visual elements
    //
    
    /** table for displaying mail server data */
    private JTable table;
    
    /** buttons */
    private JButton jBAdd    = new JButton();
    private JButton jBModify = new JButton();
    private JButton jBDelete = new JButton();
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * Creates a new instance of ResultSearchMailServerPanel
     */
    public ResultSearchMailServerPanel() {
        initGui();
    }
    
    // -------------------------------------------------------------- Properties
    
    /** model for the table component */
    private MailServerTableModel model;
    
    /**
     * Sets the mailservers to be displaied.
     * @param mailServers
     */
    public void setMailServers(MailServer[] mailServers){
        this.mailservers = mailServers;
        model.setMailServers();
    }
    
    /** selected mail server from the table */
    private MailServer selectedMailServer;
    
    /**
     * Gets the selectedMailServer property.
     */
    public MailServer getSelectedMailServer(){
        return selectedMailServer;
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Sets the action for the add button.
     * @param actionListener
     */
    public void setAddAction(ActionListener actionListener){
        jBAdd.addActionListener(actionListener);
    }
    
    /**
     * Sets the action for the modify button.
     * @param actionListener
     */
    public void setModifyAction(ActionListener actionListener){
        jBModify.addActionListener(actionListener);
    }
    
    /**
     * Sets the action for the mouse click event over a row.
     * @param mouseListener
     */
    public void setRowMouseAction(MouseListener mouseListener){
        table.addMouseListener(mouseListener);
    }
    
    /**
     * Sets the action for the delete button.
     * @param actionListener
     */
    public void setDeleteAction(ActionListener actionListener){
        jBDelete.addActionListener(actionListener);
    }
    
    public void deleteRowInTable(MailServer mailServer){
        MailServerTableModel model = (MailServerTableModel)table.getModel();
        model.deleteRow(mailServer);
        table.updateUI();
        table.clearSelection();
    }
    
    public void updateMailServer(MailServer mailServer){
        MailServerTableModel model = (MailServerTableModel)table.getModel();
        model.updateRow(mailServer);
    }
    
    /**
     * Resets the search result table (that is: no record has to be selected).
     */
    public void resetResultTableSelection() {
        
        ListSelectionModel lsm = table.getSelectionModel();
        
        //
        // clearSelection method automatically calls each ListSelectionListener
        // attached to table. Reset of the underlying table model, record selection 
        // and related gui is contained in the listener objects themselves.
        //
        // See the initGui method for the ListSelectionListener implementation.
        //
        lsm.clearSelection();
    }
    
    // -------------------------------------------------------- Override methods
    
    /**
     * Set size of the panel
     * @return dimension of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 100);
    }
    
    // --------------------------------------------------------- Private methods
    
    /**
     * Initializes visual elements.
     */
    private void initGui(){
        
        setLayout(new BorderLayout());
        
        //
        // Configure visual elements
        //
        
        // model for table data
        
        model = new MailServerTableModel();
        table = new JTable(model);
        
        // mail server table
        
        setBorder(BorderFactory.createEmptyBorder());
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        
        // buttons
        
        jBAdd.setText("Add");
        jBModify.setText("Edit");
        jBDelete.setText("Delete");
        
        enableButtons(false);
        
        jBAdd.setBounds   (new Rectangle(4, 4,  100, 20));
        jBModify.setBounds(new Rectangle(4, 34, 100, 20));
        jBDelete.setBounds(new Rectangle(4, 64, 100, 20));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);
        buttonsPanel.add(jBAdd);
        buttonsPanel.add(jBModify);
        buttonsPanel.add(jBDelete);
        
        enableButtons(false);
        
        //
        // Selected an account from the search results.
        //
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                
                if (event.getValueIsAdjusting()) {                    
                    return;
                }
                
                ListSelectionModel lsm = (ListSelectionModel)event.getSource();
                
                if (lsm.isSelectionEmpty()) {
                    
                    //
                    // No row is selected so delete and edit buttons must be
                    // disabled and selectedMailServer reset.
                    //
                    setEnableButton("delete", false);
                    setEnableButton("edit", false);
                    
                    selectedMailServer = null;
                    
                } else {
                    
                    //
                    // Exactly one row is selected so delete and edit buttons
                    // must be enabled and selectedMailServer set to the selected 
                    // one.
                    //
                    setEnableButton("delete", true);
                    setEnableButton("edit", true);
                    
                    int selectedRow = lsm.getMinSelectionIndex();
                    selectedMailServer = mailservers[selectedRow];
                }
            }
        });
        
        //
        // Add elements to this panel.
        //
        
        add(tableScrollPane, BorderLayout.WEST);
        add(buttonsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Enables/disables the edit and delete buttons.
     * @param enabled if <code>true</code> buttons are enebled, disabled
     * otherwise
     */
    private void enableButtons(boolean enabled){
        jBModify.setEnabled(enabled);
        jBDelete.setEnabled(enabled);
    }

    /**
     * Enable/disable the specified button.
     *
     * @param buttonName button name
     * @param status <code>true</code> to enable button, <code>false</code> to
     * disable it.
     */
    private void setEnableButton(String buttonName, boolean status) {
        
        JButton button = null;
        
        if (buttonName.equals("add")) {
            button = jBAdd;
        } else if (buttonName.equals("edit")) {
            button = jBModify;
        } else if (buttonName.equals("delete")) {
            button = jBDelete;
        } else {
            return; 
        }
        
        button.setEnabled(status);
    }
    
    

    // ----------------------------------------------------------- Inner classes
    
    /**
     * Model of the table used to display mail servers search results.
     */
    private class MailServerTableModel extends AbstractTableModel {
        
        // -------------------------------------------------------- Private data
        
        /** column names */
        String[] columnNames = new String[] {
            "Description",
            "Outgoing server",
            "port",
            "Incoming server",
            "port"};
        
        /**
         * Container of data in the table.
         * <p/>
         * Must be not null at the time of creation.
         */
        Object[][] rowData = new Object[0][columnNames.length];
        
        // ------------------------------------------------------ Public methods
        
        /**
         * Sets the data to be displaied wiht this panel mailservers.
         * @param mailservers
         */
        public void setMailServers() {
            
            if (mailservers == null){
                return;
            }
            
            int size = mailservers.length;
            
            if (size != 0){
                //mailservers = servers;
                rowData = new Object[size][columnNames.length];
                for (int i = 0; i < size; i++) {
                    setValueAt(mailservers[i].getDescription(), i, 0);
                    setValueAt(mailservers[i].getOutServer(), i, 1);
                    setValueAt(mailservers[i].getOutPort(), i, 2);
                    setValueAt(mailservers[i].getInServer(), i, 3);
                    setValueAt(mailservers[i].getInPort(), i, 4);
                }
            } else {
                rowData = new Object[0][columnNames.length];
            }
            table.updateUI();
        }
        
        /**
         * Deletes the table row corresponding to a given mail server.
         * @param mailServer mail sesrver to be deldeted
         */
        public void deleteRow(MailServer mailServer){
            
            if (mailServer == null){
                return;
            }
            
            int size = mailservers.length;
            if (size > 0){
                ArrayList tmp = new ArrayList();
                int index = -1;
                for (int i = 0; i < size; i++) {
                    if (mailservers[i] == mailServer){
                        index = i;
                        continue;
                    }
                    tmp.add(mailservers[i]);
                }
                
                // deletes mailServer from the displaied data
                if (index != -1){
                    ArrayList tmpDisplaied = new ArrayList();
                    for (int i = 0; i < rowData.length; i++) {
                        if (i == index){
                            continue;
                        }
                        tmpDisplaied.add(rowData[i]);
                    }
                    
                    mailservers = new MailServer[tmp.size()];
                    for (int i = 0; i < tmp.size(); i++) {
                        mailservers[i] = (MailServer)tmp.get(i);
                    }
                    rowData = new Object[tmpDisplaied.size()][columnNames.length];
                    for (int i = 0; i < tmpDisplaied.size(); i++) {
                        rowData[i] = (Object[])tmpDisplaied.get(i);
                    }
                }
                
                if (mailservers.length == 0){
                    enableButtons(false);
                }
            }
        }
        
        /**
         * Update the row of the table corresponding to the given mail server.
         * @param mailServer the mail server
         */
        public void updateRow(MailServer mailServer){
            
            if (mailServer == null){
                return;
            }
            
            int index = -1;
            for (int i = 0; i < mailservers.length; i++) {
                if (mailservers[i] == mailServer){
                    index = i;
                    break;
                }
            }
            
            if (index != -1){
                setValueAt(mailServer.getDescription(), index, 0);
                setValueAt(mailServer.getOutServer(), index, 1);
                setValueAt(mailServer.getOutPort(), index, 2);
                setValueAt(mailServer.getInServer(), index, 3);
                setValueAt(mailServer.getInPort(), index, 4);
                
                updateUI();
                
            }
        }
        
        // ---------------------------------------------------- Override methods
        
        public int getRowCount() {
            return rowData.length;
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
        
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData[rowIndex][columnIndex];
        }
        
        public void setValueAt(Object value, int rowIndex, int colIndex) {
            rowData[rowIndex][colIndex] = value;
            fireTableCellUpdated(rowIndex, colIndex);
        }
    }
}
