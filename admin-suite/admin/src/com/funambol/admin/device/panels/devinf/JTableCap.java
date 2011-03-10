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

import java.util.Vector;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Extension of JTable that allows a column to have different row editors
 * models
 *
 * @version $Id: JTableCap.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class JTableCap extends JTable {

    // ---------------------------------------------------------- Protected data
    protected RowEditorModel rm;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructs a new JTableCap. The row editor model is set to null
     */
    public JTableCap() {
        super();
        rm = null;
    }

    /**
     * Constructs a JTableCap that is initialized with table model, a default
     * column model, a default selection model and row editor model is set to
     * null.
     *
     * @param tm model for the table
     */
    public JTableCap(TableModel tm) {
        super(tm);
        rm = null;
    }

    /**
     * Constructs a JTableCap that is initialized with the given parameters
     * table model and column model, a default selection model and row editor
     * model is set to null.
     *
     * @param tm model for the table
     * @param cm column model for table
     */
    public JTableCap(TableModel tm, TableColumnModel cm) {
        super(tm, cm);
        rm = null;
    }

    /**
     * Constructs a JTableCap that is initialized with the given parameters
     * table model, column model and selection model; row editor model is set to
     * null.
     *
     * @param tm model for the table
     * @param cm column model for table
     * @param sm selection model for table
     */
    public JTableCap(TableModel tm, TableColumnModel cm,
                     ListSelectionModel sm) {
        super(tm, cm, sm);
        rm = null;
    }

    /**
     * Constructs a JTableCap with the given rows and columns number of empty
     * cells using DefaultTableModel
     *
     * @param rows number of rows for the table
     * @param cols number of cols for the table
     */
    public JTableCap(int rows, int cols) {
        super(rows, cols);
        rm = null;
    }

    /**
     * Constructs a JTableCap which displays the values in the Vector of
     * Vectors, rowData, with column names, columnNames
     *
     * @param rowData     rows to be displayed in table
     * @param columnNames columns headers to be displayed in table
     */
    public JTableCap(final Vector rowData, final Vector columnNames) {
        super(rowData, columnNames);
        rm = null;
    }

    /**
     * Constructs a JTableCap which displays the values in the objects array of
     * arrays, rowData, with column names, columnNames; row editor model is set
     * to null.
     *
     * @param rowData  rows to be displayed in table
     * @param colNames columns headers to be displayed in table
     */
    public JTableCap(final Object[][] rowData, final Object[] colNames) {
        super(rowData, colNames);
        rm = null;
    }

    /**
     * Constructs a JTableCap that is initialized with the given parameters
     * table model row editor model.
     *
     * @param tm model for the table
     * @param rm row editor model for the table
     */
    public JTableCap(TableModel tm, RowEditorModel rm) {
        super(tm, null, null);
        this.rm = rm;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Sets the row editor model for the table
     *
     * @param rm roe wditor model to be set to the table
     */
    public void setRowEditorModel(RowEditorModel rm) {
        this.rm = rm;
    }

    /**
     * Returns the current row editor model of the table.
     *
     * @return the current row editor model of the table
     */
    public RowEditorModel getRowEditorModel() {
        return rm;
    }

    /**
     * Returns the cell editor for a specific cell
     *
     * @param row the row of the cell
     * @param col the col of the cell
     *
     * @return the cell editor of the cell at specified row and column
     */
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor tmpEditor = null;
        if (rm != null)
            tmpEditor = rm.getEditor(row);
        if (tmpEditor != null)
            return tmpEditor;
        return super.getCellEditor(row, col);
    }
}
