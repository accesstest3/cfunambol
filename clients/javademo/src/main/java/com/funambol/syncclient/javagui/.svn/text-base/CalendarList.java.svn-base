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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.funambol.common.pim.common.*;

/**
 * The calendar list panel.
 *
 * @version $Id: CalendarList.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class CalendarList
extends Panel
implements ActionListener,
           ItemListener,
           MouseListener,
           ConfigurationParameters {

    //---------------------------------------------------------------- Constants
    //
    // Approximate width of the vertical scrollbar for the List component
    //
    private static final int LIST_SCROLLBAR_WIDTH = 25;

    //------------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow mw        = null;
    private List       list      = null;
    private Button     butModify = null;
    private Button     butDelete = null;
    private Button     butNew    = null;
    private Language   ln        = new Language();

    //----------------------------------------------------------- Public methods

    /**
     * Creates the panel.
     *
     * @param mw the window containing this panel
     */
    public CalendarList(MainWindow mw) {

        Label  title           = null;
        Button butSync         = null;

        Panel  editButtonPanel = null;
        Panel  buttonPanel     = null;

        this.mw = mw;

        setLayout(new BorderLayout());
        title = new Label(ln.getString("calendar_list"));
        list  = new List (10);
        list.addItemListener (this);
        list.addMouseListener(this);

        butNew = new Button     (ln.getString("new"));
        butNew.setActionCommand ("new");
        butNew.addActionListener(this);

        butModify = new Button     (ln.getString("modify"));
        butModify.setActionCommand ("modify");
        butModify.addActionListener(this);
        butModify.setEnabled       (false);

        butDelete = new Button     (ln.getString("delete"));
        butDelete.setActionCommand ("delete");
        butDelete.addActionListener(this);
        butDelete.setEnabled       (false);

        butSync = new Button     (ln.getString("synchronize"));
        butSync.setActionCommand ("sync");
        butSync.addActionListener(this);

        editButtonPanel = new Panel();
        editButtonPanel.setLayout(new GridLayout(1,3));
        editButtonPanel.add(butNew   );
        editButtonPanel.add(butModify);
        editButtonPanel.add(butDelete);

        buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(2,1));
        buttonPanel.add(editButtonPanel);
        buttonPanel.add(butSync        );

        add(title      , BorderLayout.NORTH );
        add(list       , BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH );
    }

    /**
     * Invoked when an action occurs (i.e. a button is pressed).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().equals("sync")         ) {            
            DemoMenuBar menuBar = (DemoMenuBar)mw.getMenuBar();
            menuBar.setEnableMenu(false);            
            
            mw.show(KEY_SYNC);
            
            menuBar.setEnableMenu(true);                        
        } else if (evt.getActionCommand().equals("modify")) {
            mw.show(KEY_CALENDARMODIFY);
        } else if (evt.getActionCommand().equals("delete")) {
            mw.deleteCalendar();
        } else if (evt.getActionCommand().equals("new"   )) {
            mw.show(KEY_CALENDARNEW);
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     * This method sets the current index to the index of the selected idem and
     * enables the Modify and Delete buttons.
     *
     * @param evt the occurred event
     */
    public void itemStateChanged(ItemEvent evt) {
        mw.setCurrentIndex((Integer) evt.getItem());
        butModify.setEnabled(true);
        butDelete.setEnabled(true);
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on 
     * a component.
     * This method opens the modification page for the selected calendar if a 
     * double click is detected.
     *
     * @param evt the occurred event
     */
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount()>=2) {
            if (butModify.isEnabled() &&
                evt.getX()<=(getSize().width - LIST_SCROLLBAR_WIDTH) &&
                evt.getY() <= (mw.calendars.size() * list.getPreferredSize().height / 10)) {
                mw.show(KEY_CALENDARMODIFY);
            }
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mousePressed(MouseEvent evt) {
        // do nothing
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseReleased(MouseEvent evt) {
        // do nothing
    }

    /**
     * Invoked when the mouse enters a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseEntered(MouseEvent evt) {
        // do nothing
    }

    /**
     * Invoked when the mouse exits a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseExited(MouseEvent evt) {
        // do nothing
    }

    //-------------------------------------------------------- Protected methods

    /**
     * Fills the list with the loaded calendars.
     * This method also disables the Modify and Delete buttons.
     */
    protected void fillList() {
        list.removeAll();

        String       tmpDisplayName = null;
        String       tmpDescription = null;
        DemoCalendar tmpCalendar    = null;

        for (int i=0, l = mw.calendars.size(); i < l; i++) {
            tmpCalendar = (DemoCalendar) mw.calendars.elementAt(i);
            tmpDisplayName = tmpCalendar.getCalendar().getEvent()
                    .getSummary().getPropertyValueAsString();

            tmpDescription = tmpCalendar.getCalendar().getEvent()
                    .getDescription().getPropertyValueAsString();

            if (tmpDisplayName  != null && !tmpDisplayName.equals("")) {
                list.add(tmpDisplayName);
            } else if (tmpDescription != null && !tmpDescription.equals("")) {
                list.add(tmpDescription);
            } else {
                tmpDisplayName = "Unknown";
                list.add(tmpDisplayName);
            }
        }

        //
        // Disable buttons: no item can be already selected
        //
        butModify.setEnabled(false);
        butDelete.setEnabled(false);
    }

}
