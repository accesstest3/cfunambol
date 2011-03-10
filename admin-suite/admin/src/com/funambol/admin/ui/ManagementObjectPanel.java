/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.funambol.framework.engine.source.SyncSource;

import com.funambol.admin.AdminException;
import com.funambol.admin.mo.ManagementObject;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;


/**
 * This class represents a basic management panel. A management panel is a 
 * generic panel used to edit the content of a complex object such as a sync
 * source or a server configuration bean. The object under editing is set by
 * the admin framework calling <i>setObject(...)</i> and retrieved calling
 * <i>getObject()</i>. Such object is stored in the protected field <i>object</i>
 * for future references.
 * <p>
 * Concrete implementations shall show the object properties on <i>setObejct()</i>
 * and update <i>object</i> before returning from <i>getObject()</i>.
 * 
 * @version $Id: ManagementObjectPanel.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 */

public abstract class ManagementObjectPanel 
extends JPanel 
implements Serializable {

    // --------------------------------------------------------------- Constants

    public final static Font defaultFont = new Font("Arial", 0, 10);
    public final static Font defaultTableFont = new Font("Arial", 0, 10);
    public final static Font defaultTableHeaderFont = new Font("Arial", 1, 11);
    public final static Font titlePanelFont = new java.awt.Font("Arial", 1, 14);
    public final static Font loginPanelFont = new java.awt.Font("Arial", 0, 11);
    
    public final static byte ACTION_EVENT_INSERT = 1;
    public final static byte ACTION_EVENT_UPDATE = 2;
    public final static byte ACTION_EVENT_DELETE = 3;
    
    // ---------------------------------------------------------- Protected data
    
    /**
     * The object under editing
     */
    protected ManagementObject mo;
    
    /**
     * The registered action listeners
     */
    protected List listeners;
    
    // ------------------------------------------------------------ Constructors

    /**
     * Constructs a new ManagementObjectPanel
     */
    public ManagementObjectPanel() {
        mo = null;
        listeners = new ArrayList();
    }

    // -------------------------------------- Management panel lifecycle methods

    /**
     * Sets the object under editing by this management panel
     *
     * @param o the object to be edited
     *
     */
    public void setManagementObject(ManagementObject o) {
        mo = o;
    }
    
    /**
     * Returns the edited object 
     *
     * @return the edited object
     */
    public ManagementObject getManagementObject() {
        return mo;
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Register the given action listener
     *
     * @param l the action listener
     */
    public void addActionListener(ActionListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }
    
    /**
     * De-register the given action listener
     *
     * @param l the action listener
     */
    public void removeActionListener(ActionListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    
    /** 
     * Called when an error related to tthe panel should be displayed
     *
     * @param e the AdminException occurred
     */
    public void notifyError(AdminException e) {
        Log.error(e);
        JOptionPane.showMessageDialog( 
            null, 
            e.getMessage(), 
            Bundle.getMessage(Bundle.ERROR), 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // ------------------------------------------------------- Protected methods
    
    /**
     * Notifies an action event to all listeners
     *
     * @param e the event
     */
    protected void actionPerformed(ActionEvent e) {
        Iterator i = listeners.iterator();
        
        while (i.hasNext()) {
            ((ActionListener)i.next()).actionPerformed(e);
        }
    }
    
    // -------------------------------------------------------- Abstract methods
    
    /**
     * Tells the panel that it has to update the UI with the values in the 
     * management object.
     */
    public abstract void updateForm();

}
