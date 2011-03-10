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

import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.mo.ManagementObject;

import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Table model for the tables in data transformer manager screen
 * 
 * @version $Id: DataTransformerManagerTableModel.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 */
public class DataTransformerManagerTableModel  extends AbstractTableModel{
    
    // ------------------------------------------------------------ Private data
    /** data to be displayed in table */
    private Map map = null;
    /** the names of the columns */
    private String[] columns = {};   
    /** list of keys of map **/
    private ArrayList keys = null;
    // ------------------------------------------------------------ Constructor     
    /**
     *  Creates a new instance of DataTransformerManagerTableModel
     *
     *  @param map the hasmap with the informations to be displayed in the table
     *  @param columns array of string with the names of the columns
     */
    public DataTransformerManagerTableModel(Map map,String[] columns) {        
        this.map=map;        
        this.columns=columns;
        keys=new ArrayList();
        Iterator iter=map.keySet().iterator();             
        while(iter.hasNext()) {
            keys.add(iter.next());            
        }        
    }
    
    // ----------------------------------------------------------- Public Methods
        
    /**
     *  Sets the names of the columns of the table
     *
     *  @param columns the array with the names of columns
     */
    public void setColumns(String[] columns) {
        this.columns=columns;
    }
    
    /**
     * Returns the array of string with the names of columns
     *
     *@return the array with the names of columns
     */
    public String[] getColumns() {
        return this.columns;
    }

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {       
        return keys.size();
    }

    /**
     * Returns the name of the column at col
     *
     * @param col the index of the column
     *
     * @return the name of the column
     */
    public String getColumnName(int col) {
        return columns[col];
    }

    /**
     * Returns the number of columns in the model
     *
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns true if the cell at row and col is editable. Otherwise,
     * setValueAt on the cell will not change the value of that cell.
     *
     * @param row the row whose value to be queried
     * @param col the column whose value to be queried
     *
     * @return true if the cell is editable
     */
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case 0: //Name
                return true;                
            case 1: //value
                return true;                           
        }
        return false;
    }

    /**
     * Returns the value for the cell at col and row.
     *
     * @param row the row whose value is to be queried
     * @param col the column whose value is to be queried
     *
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int row, int col) {
        
        if (col == 0) {            
            return keys.get(row);
        } else if (col == 1) {
            return map.get(keys.get(row));          
        }
        return "";
    }

    /**
     * Sets the value in the cell at col and row to value.
     *
     * @param value the new value
     * @param row   the row whose value is to be changed
     * @param col   the column whose value is to be changed
     */
    public void setValueAt(Object value, int row, int col) {
        if(value.toString().equals(""))
        {            
            return;
        }
        
        String key=(String)keys.get(row);
        if (col == 0) {
            if(!key.equals(value)) {
                Object oldVal=map.get(key);
                map.remove(key);
                map.put(value,oldVal);
                keys.set(row,value);
                fireTableDataChanged();
            }
        } else if(col==1) {            
            Object oldValue=map.get(key);
            if(!oldValue.equals(value)) {
                map.put(key,value);
                fireTableDataChanged();
            }
        }        
    }
    
    /**
     * Adds a new element to the hashMap.
     * Defalut values are read from properties file
     */
    public int addItem()
    {
        String newItem=Bundle.getMessage(Bundle.DTM_NEW_ITEM);
        String key=newItem;
        int i=1;
        while(map.get(key)!=null)
        {
            key=newItem+i;
            i++;
        }
        map.put(key,newItem);
        keys.add(key);
        
        fireTableDataChanged();
        
        return keys.size()-1;
    }
    
    /**
     * Removes an item from table.     
     */
    public void removeItem(int row)
    {
        if(row<0)
        {
            return;
        }
        //find the item in map and remove
        int answer = JOptionPane.showConfirmDialog(
                        null, Bundle.getMessage(
                                Bundle.DTM_MSG_DELETE),
                        Bundle.getMessage(
                                Bundle.DTM_MSG_DELETE_TITLE),
                        JOptionPane.YES_NO_OPTION);

        if (answer == 0) {
            String key=(String)keys.get(row);
            map.remove(key);
            keys.remove(row);
            fireTableDataChanged();            
        }
        
    }
    
}
