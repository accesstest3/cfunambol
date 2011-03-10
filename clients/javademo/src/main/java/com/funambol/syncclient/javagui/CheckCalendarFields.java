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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The new calendar insertion panel.
 *
 * @version $Id: CheckCalendarFields.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class CheckCalendarFields 
extends Frame
implements ActionListener, WindowListener, ConfigurationParameters {

    // ------------------------------------------------------------ Private data

    /**
     * The window containing this panel
     */
    private MainWindow   mw   = null;

    private CalendarForm form = null;
    private Label        lb1  = null;
    private Label        lb2  = null;
    private Label        lb3  = null;

    private Language     ln   = new Language();

    // ---------------------------------------------------------- Public methods
    public CheckCalendarFields(MainWindow mw, CalendarForm form) {

        super();
        setTitle(ln.getString("check_calendar_fields"));

        Image  icon      = null;
        Button butOk     = null;
        Panel  textPanel = null;

        icon = Toolkit.getDefaultToolkit().getImage(FRAME_ICONNAME);

        setIconImage(icon);

        this.mw   = mw  ;
        this.form = form;

        setSize(300, 150);
        setLocation(400, 200);
        setLayout(new BorderLayout());

        lb1 = new Label("", Label.CENTER);
        lb2 = new Label("", Label.CENTER);
        lb3 = new Label("", Label.CENTER);

        butOk = new Button(ln.getString("ok"));
        butOk.setActionCommand("ok");
        butOk.addActionListener(this);

        textPanel = new Panel();
        textPanel.setLayout(new GridLayout(3, 1));
        textPanel.add(lb1);
        textPanel.add(lb2);
        textPanel.add(lb3);

        add(textPanel, BorderLayout.CENTER);
        add(butOk    , BorderLayout.SOUTH );

        addWindowListener(this);
    }

    public boolean checkForAllDay() {

        String startDate = null;
        String endDate   = null;

        startDate = FieldsHelper.dateNormalize(form.getStartDate());
        endDate   = FieldsHelper.dateNormalize(form.getEndDate()  );
        
        if (startDate          == null ||
            startDate.length() == 0    ||
            (!FieldsHelper.checkDate(startDate, FieldsHelper.DATE_FORMAT))) {
           lb1.setText(ln.getString ("please_insert_valid_start_date_time"));
           return false;
        }

        if (endDate == null || endDate.length() == 0) {
            form.setEndDate(startDate);
        } else if (!FieldsHelper.checkDate(endDate, FieldsHelper.DATE_FORMAT)) {
           lb1.setText(ln.getString ("please_insert_valid_end_date_time"));
           return false;
        }
        
        if(!FieldsHelper.compareStartDateEndDate(startDate + " 00:00:00",
                endDate + " 00:00:00")) {
            lb1.setText(ln.getString ("end_date_time_before_start_date_time"));
            return false;
        }

        return true;
    }

    public boolean areFieldsRight() {

        String startDate = null;
        String endDate   = null;
        String startTime = null;
        String endTime   = null;

        startDate = FieldsHelper.dateNormalize(form.getStartDate());
        endDate   = FieldsHelper.dateNormalize(form.getEndDate  ());
        startTime = FieldsHelper.timeNormalize(form.getStartTime());
        endTime   = FieldsHelper.timeNormalize(form.getEndTime  ());

        if (form.isAllDayEvent()) {
            return checkForAllDay();
        }

        if (startDate          == null ||
            startDate.length() == 0    ||
            startTime          == null ||
            startTime.length() == 0    ||
            (!FieldsHelper.checkDate(startDate + " " + startTime + ":00", FieldsHelper.DATE_FORMAT_AND_TIME))) {
           lb1.setText(ln.getString ("please_insert_valid_start_date_time"));
           return false;
        }

        if (endDate          == null ||
            endDate.length() == 0    ||
            endTime          == null ||
            endTime.length() == 0    ||
            (!FieldsHelper.checkDate(endDate + " " + endTime + ":00",   FieldsHelper.DATE_FORMAT_AND_TIME))) {
           lb1.setText(ln.getString ("please_insert_valid_end_date_time"));
           return false;
        }

        if(!FieldsHelper.compareStartDateEndDate(startDate + " " + startTime + ":00",
                endDate + " " + endTime + ":00")) {
            lb1.setText(ln.getString ("end_date_time_before_start_date_time"));
            return false;
        }

        return true;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("ok")) {
            mw.setEnabled(true);
            setVisible(false);
        }
    }

    public void windowClosing(WindowEvent evt) {
        mw.setEnabled(true);
        setVisible(false);
    }

    public void windowActivated  (WindowEvent e) {}

    public void windowClosed     (WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowIconified  (WindowEvent e) {}

    public void windowOpened     (WindowEvent e) {}

}
