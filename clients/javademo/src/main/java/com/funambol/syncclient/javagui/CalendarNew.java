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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The new Calendar insertion panel.
 *
 * @version $Id: CalendarNew.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class CalendarNew
extends Panel
implements ActionListener, ConfigurationParameters {

    //------------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow          mw      = null;
    private CalendarForm        form    = null;
    private CheckCalendarFields cfields = null;
    private Language            ln      = new Language();

    //----------------------------------------------------------- Public methods

    /**
     * Creates the panel.
     *
     * @param mw the window containing this panel
     */
    public CalendarNew(MainWindow mw) {

        Label  title       = null;
        Button butOk       = null;
        Button butCancel   = null;
        Panel  buttonPanel = null;

        this.mw = mw;

        setLayout(new BorderLayout());
        title = new Label(ln.getString("new_calendar"));

        form = new CalendarForm(mw);

        butOk = new Button(ln.getString("ok"));
        butOk.setActionCommand("ok");
        butOk.addActionListener(this);

        butCancel = new Button(ln.getString("cancel"));
        butCancel.setActionCommand("cancel");
        butCancel.addActionListener(this);

        buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(butOk    );
        buttonPanel.add(butCancel);

        add(title      , BorderLayout.NORTH );
        add(form       , BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH );
    }

    /**
     * Invoked when an action occurs (i.e. a button is pressed).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().equals("ok")) {

                cfields = new CheckCalendarFields(mw, form);
                if (!cfields.areFieldsRight()) {
                    cfields.setVisible(true);
                    mw.setEnabled(false);
                }
                else {
                    saveCalendar();
                }

        } else if (evt.getActionCommand().equals("cancel")) {
            mw.show(KEY_CALENDARLIST);
        }
    }

    //-------------------------------------------------------- Protected methods

    /**
     * Sets all the textfields in the DemocCalendar subpanel to an empty string.
     */
    protected void blankFields() {
        form.blankFields();
    }

    //---------------------------------------------------------- Private methods

    /**
     * Saves the new Calendar.
     */
    private void saveCalendar() {
        mw.writeNewCalendar(form.getFields());
        mw.show(KEY_CALENDARLIST);
    }
}
