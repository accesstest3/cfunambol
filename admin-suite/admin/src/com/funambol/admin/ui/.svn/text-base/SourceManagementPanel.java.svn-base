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

import java.awt.Font;
import java.io.Serializable;

import javax.swing.JPanel;

import com.funambol.framework.engine.source.SyncSource;

import com.funambol.admin.AdminException;

/**
 * Rapresents the base class for the SyncSources management panels
 * 
 * @version $Id: SourceManagementPanel.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 */

public abstract class SourceManagementPanel 
extends ManagementObjectPanel 
implements Serializable {

    // --------------------------------------------------------------- Constants

    // These are the possible states of the SourceManagementPanel
    public static final int STATE_INSERT = 0;
    public static final int STATE_UPDATE = 1;

    // -------------------------------------------------------------- Properties

    /**
     * Current state of the panel
     */
    private int state = STATE_INSERT;

    /**
     * Sets property state
     * @beaninfo
     *     description: current state of the SourceManagementPanel
     *     displayName: state
     */
    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Constructs a new SourceManagementPanel
     */
    public SourceManagementPanel() {
        super();
    }

    // ------------------------------------------------------- Protected Methods
    
    public SyncSource getSyncSource() {
        if (mo == null) {
            return null;
        }
        
        return (SyncSource)mo.getObject();
    }
}
