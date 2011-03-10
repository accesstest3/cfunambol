/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003-2007 Funambol, Inc.
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

package com.funambol.syncclient.javagui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The menu for this GUI.
 *
 *
 *
 * @version $Id: DemoMenuBar.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class DemoMenuBar
extends MenuBar
implements ActionListener, ConfigurationParameters {

    //---------------------------------------------------------- Constants

    //---------------------------------------------------------- Private data

    //
    // The window containing this menubar
    //
    private MainWindow  mw        = null           ;
    private About       ppcAbout  = null           ;

    private Language    ln        = new Language() ;

    //---------------------------------------------------------- Public methods

    /**
     * Creates the panel.
     *
     * @param mw the window containing this panel
     */
    public DemoMenuBar(MainWindow mw) {

        MenuItem mi        = null;
        Menu     menuFile  = null;
        Menu     menuEdit  = null;
        Menu     menuView  = null;
        Menu     menuHelp  = null;

        this.mw = mw;

        //
        // File Menu
        //
        menuFile = new Menu(ln.getString("file"));

        menuFile.add(mi = new MenuItem (ln.getString ("synchronize"   )) ) ;
        mi.addActionListener(this);
        menuFile.add(mi = new MenuItem (ln.getString ("configure"     )) ) ;
        mi.addActionListener(this);
        menuFile.addSeparator();
        menuFile.add(mi = new MenuItem (ln.getString ("exit"          )) ) ;
        mi.addActionListener(this);

        //
        // Edit Menu
        //
        menuEdit = new Menu(ln.getString("edit"));
        menuEdit.add(mi = new MenuItem (ln.getString ("new_contact"   )) );
        mi.addActionListener(this);
        menuEdit.add(mi = new MenuItem (ln.getString ("new_calendar"  )) );
        mi.addActionListener(this);

        //
        // View Menu
        //
        menuView = new Menu(ln.getString("view"));
        menuView.add(mi = new MenuItem (ln.getString ("contact_list"  )) );
        mi.addActionListener(this);
        menuView.add(mi = new MenuItem (ln.getString ("calendar_list" )) );
        mi.addActionListener(this);

        //
        // Help Menu
        //
        menuHelp =        new Menu     (ln.getString ("help"          ))  ;
        menuHelp.add(mi = new MenuItem (ln.getString ("about"         ))) ;
        mi.addActionListener(this);

        //
        // About Window
        //
        ppcAbout = new About(mw);

        add (menuFile ) ;
        add (menuEdit ) ;
        add (menuView ) ;
        add (menuHelp ) ;
    }

    /**
     * Invoked when an action occurs (i.e. a menu item is selected).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {

        String item = evt.getActionCommand();

        if (item.equals(ln.getString("exit"))) {
            mw.exit();
        } else if (item.equals (ln.getString ("configure"     ))) {
            mw.show(KEY_CONFIG);
        } else if (item.equals (ln.getString ("contact_list"  ))) {
            mw.show(KEY_CONTACTLIST);
        } else if (item.equals (ln.getString ("new_contact"   ))) {
            mw.show(KEY_CONTACTNEW);
        } else if (item.equals (ln.getString ("calendar_list" ))) {
             mw.show(KEY_CALENDARLIST);
        } else if (item.equals (ln.getString ("new_calendar"  ))) {
            mw.show(KEY_CALENDARNEW);
        } else if (item.equals (ln.getString ("synchronize"   ))) {
            mw.show(KEY_SYNC);
        } else if (item.equals (ln.getString ("about"         ))) {
            mw.setEnabled(false);
            ppcAbout.show();
        }
    }
    
    /**
     * Enable/disable the java gui menu.
     * @param enabled must be set to <code>true</code> to enable the menu bar,
     * <code>false</code> to disable it.
     */
    public void setEnableMenu(boolean enabled){        
        for (int i = 0; i < this.getMenuCount(); i++) {
            MenuItem menuItem = this.getMenu(i);
            menuItem.setEnabled(enabled);
        }
    }

    //---------------------------------------------------------- Private methods

}
