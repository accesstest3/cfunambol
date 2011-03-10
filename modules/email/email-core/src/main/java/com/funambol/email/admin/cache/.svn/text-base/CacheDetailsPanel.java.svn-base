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
package com.funambol.email.admin.cache;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.SyncItemInfoAdmin;
import com.funambol.email.admin.dao.WSDAO;

/**
 * Panel to show each cache entry for a given (username, protocol).
 * @version $Id: CacheDetailsPanel.java,v 1.1 2008-03-25 11:28:18 gbmiglia Exp $
 */
public class CacheDetailsPanel extends JPanel {

    // ------------------------------------------------------------ Private data

    private String username;
    private String protocol;
    private SyncItemInfoAdmin[] cacheEntries;

    //
    // Visual elements
    //

    private JLabel panelName = new JLabel();

    /** table for displaying mail server data */
    private JTable table;

    private JButton jBClose = new JButton();

    // -------------------------------------------------------------- Properties

    /** data access object */
    private WSDAO WSDao;

    /**
     * Sets the WSDao property.
     * @param WSDao data access object
     */
    public void setWSDao(WSDAO WSDao) {
        this.WSDao = WSDao;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of CacheDetailsPanel
     * @param username username these entries refer to
     * @param protocol protocol these entries refer to
     */
    public CacheDetailsPanel(String username, String protocol) {

        this.username = username;
        this.protocol = protocol;
        initGui();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Initialize visual elements.
     */
    private void initGui(){

        setLayout(null);

        //
        // Configure visual elements
        //

        // title

        panelName.setFont(GuiFactory.titlePanelFont);

        StringBuilder sb = new StringBuilder();
        sb.append("Cache for ").append(username).append(',').append(protocol);
        panelName.setText(sb.toString());

        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(new TitledBorder(""));

        // cache entries table

        table = new JTable(new CacheEntriesTableModel());
        setBorder(BorderFactory.createEmptyBorder());
        table.setShowGrid(true);
        table.setAutoscrolls(true);
        //table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFont(GuiFactory.defaultTableFont);
        table.getTableHeader().setFont(GuiFactory.defaultTableHeaderFont);
        tableScrollPane.setBounds(new Rectangle(0, 53, 864, 330));

        // table columns

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int tableWidth = this.getWidth();
        //int columnWidth = 100;
        TableColumnModel columnModel = table.getColumnModel();
        int numCol = columnModel.getColumnCount();
        for (int i = 0; i < numCol; i++) {
            TableColumn column = columnModel.getColumn(i);
            switch (i){
                case 0:
                    column.setMinWidth(58);
                    break;
                case 5:

                    int width = tableScrollPane.getWidth() - (62 + 160*4);
                    if (width > 0){
                        column.setMinWidth(width);
                    } else {
                        column.setMinWidth(160);
                    }
                    break;
                default:
                    column.setMinWidth(160);
            }
        }

        int vColIndex = 0;
        TableColumn col = table.getColumnModel().getColumn(vColIndex);
        int width = 20;
        col.setPreferredWidth(width);

        // buttons

        jBClose.setText("Close");
        jBClose.setBounds(new Rectangle(380, 400, 100, 20));

        //
        // Add elements to this panel.
        //

        add(panelName);
        add(tableScrollPane, BorderLayout.WEST);
        add(jBClose);

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Retrieve cache entries for this panel username and password.
     */
    public void loadCacheEntries(){
        try {
            cacheEntries = WSDao.getCachedInfo(username, protocol);

            CacheEntriesTableModel model =
                    (CacheEntriesTableModel)table.getModel();
            model.loadEntries(cacheEntries);

        } catch (InboxListenerException e) {
            Log.error(e);
        }
    }

    /**
     * Sets the action bound to the cancel button.
     */
    public void setCloseAction(ActionListener actionListener){
        jBClose.addActionListener(actionListener);
    }

    // ----------------------------------------------------------- Inner classes

    /**
     * Model of the table used to display cache entries.
     */
    private class CacheEntriesTableModel extends AbstractTableModel {

            /** column names */
            String[] columnNames = new String[] {
                "N.",
                "GUID",
                "Message Id",
                "Header date",
                "Header received",
                "Subject"};

            /**
             * Container of data in the table.
             * <p/>
             * Must be not null at the time of creation.
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
            public void setValueAt(Object value, int rowIndex, int colIndex) {
                rowData[rowIndex][colIndex] = value;
                fireTableCellUpdated(rowIndex, colIndex);
            }

            /**
             * Fill table rows with given cache entries.
             */
            public void loadEntries(SyncItemInfoAdmin[] entries){

                int size = entries.length;
                if (size != 0){
                    cacheEntries = entries;
                    rowData = new Object[size][columnNames.length];
                    for (int i = 0; i < cacheEntries.length; i++) {
                        setValueAt(Integer.toString(i + 1), i, 0);
                        setValueAt(entries[i].getGuid(), i, 1);
                        setValueAt(entries[i].getMessageID(), i, 2);
                        setValueAt(entries[i].getHeaderDate(), i, 3);
                        setValueAt(entries[i].getHeaderReceived(), i, 4);
                        setValueAt(entries[i].getSubject(), i, 5);
                    }
                    table.updateUI();
                }
            }
        };
}
