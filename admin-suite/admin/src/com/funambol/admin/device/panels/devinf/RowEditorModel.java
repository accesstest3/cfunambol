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

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;

/**
 * Class used with JTableCap to allow a column to have different row editors
 *
 * @version $Id: RowEditorModel.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class RowEditorModel {

    // ------------------------------------------------------------ Private data
    private Hashtable data;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a RowEditorModel used for a collumn that needs to have a
     * different row editor
     */
    public RowEditorModel() {
        data = new Hashtable();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Adds a cell editor for a specific row
     *
     * @param row the row for wich the row editor is added
     * @param e   the editor to be added the row
     */
    public void addEditorForRow(int row, TableCellEditor e) {
        data.put(new Integer(row), e);
    }

    /**
     * Removes a cell editor for a specific row
     *
     * @param row the row for wich the row editor is added
     */
    public void removeEditorForRow(int row) {
        data.remove(new Integer(row));
    }

    /**
     * Returns the cell editor for a specific row
     *
     * @param row the row for wich the row editor is added
     *
     * @return returns the cell editor for the specified row
     */
    public TableCellEditor getEditor(int row) {
        return (TableCellEditor) data.get(new Integer(row));
    }
}
