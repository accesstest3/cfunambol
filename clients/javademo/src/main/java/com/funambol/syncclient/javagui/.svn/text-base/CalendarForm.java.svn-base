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
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;

import java.text.ParseException;

import javax.swing.JTextField;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * A form containing all the fields concerning a calendar.
 * This form will be a subpanel for CalendarModify and CalendarNew
 *
 * @version $Id: CalendarForm.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class CalendarForm
extends Panel
implements ActionListener,
           MouseListener ,
           ItemListener  ,
           ConfigurationParameters {

    //------------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow mw = null;

    private Language ln = new Language();

    private JTextField tf1 = null;
    private JTextField tf2 = null;
    private JTextField tf3 = null;
    private JTextField tf4 = null;
    private JTextField tf5 = null;
    private JTextField tf6 = null;
    private JTextField tf7 = null;

    private Checkbox   checkAllDayEvent  = null;

    private CardLayout cardLayout        = null;
    private Panel      fieldPanel        = null;
    private ScrollPane scrollGeneralPane = null;

    private Calendar   calendar          = null;

    //----------------------------------------------------------- Public methods
    public String getStartDate() {
        return tf2.getText();
    }
    public String getStartTime() {
        return tf6.getText();
    }
    public String getEndDate() {
        return tf3.getText();
    }
    public String getEndTime() {
        return tf7.getText();
    }
    public void setEndDate(String date) {
        tf3.setText(date);
    }
    public boolean isAllDayEvent() {
        return checkAllDayEvent.getState();
    }

    /**
     * Creates the form subpanel.
     *
     * @param mw the window containing this panel
     */
    public CalendarForm(MainWindow mw) {

        this.mw = mw;

        Panel generalPanel = new Panel();
        generalPanel.setLayout(null);

        int x1  = 0;
        int x2  = 220;
        int y   = 0;
        int dy  = 22;
        int h   = 22;
        int w   = 210;

        //
        // Subject
        //
        Label lb1 = new Label(ln.getString("summary"   ));
        Label lb2 = new Label(ln.getString("start_date"));
        Label lb3 = new Label(ln.getString("end_date"  ));
        Label lb4 = new Label(ln.getString("location"  ));

        //
        // body
        //
        Label lb5 = new Label(ln.getString("description"));
        Label lb6 = new Label(ln.getString("start_time" ));
        Label lb7 = new Label(ln.getString("end_time"   ));

        Label lb8 = new Label(ln.getString("all_day_event"));

        tf1 = new JTextField();
        tf1.setBackground(java.awt.SystemColor.window);
        tf2 = new JTextField();
        tf2.setBackground(java.awt.SystemColor.window);
        tf3 = new JTextField();
        tf3.setBackground(java.awt.SystemColor.window);
        tf4 = new JTextField();
        tf4.setBackground(java.awt.SystemColor.window);
        tf5 = new JTextField();
        tf5.setBackground(java.awt.SystemColor.window);
        tf6 = new JTextField();
        tf6.setBackground(java.awt.SystemColor.window);
        tf7 = new JTextField();
        tf7.setBackground(java.awt.SystemColor.window);

        setNextFocusableProcedure();

        checkAllDayEvent = new Checkbox(null);
        checkAllDayEvent.addItemListener(this);

        tf1.addMouseListener(this);
        tf2.addMouseListener(this);
        tf3.addMouseListener(this);
        tf4.addMouseListener(this);
        tf5.addMouseListener(this);
        tf6.addMouseListener(this);
        tf7.addMouseListener(this);


        // Set bounds of the components
        lb1.setBounds(x1, y, w, h);
        tf1.setBounds(x2, y, w, h);

        y += dy;

        lb2.setBounds(x1, y, w, h);
        tf2.setBounds(x2, y, w, h);

        y += dy;

        lb6.setBounds(x1, y, w, h);
        tf6.setBounds(x2, y, w, h);

        y += dy;

        lb3.setBounds(x1, y, w, h);
        tf3.setBounds(x2, y, w, h);

        y += dy;

        lb7.setBounds(x1, y, w, h);
        tf7.setBounds(x2, y, w, h);

        y += dy;

        lb8.setBounds(x1, y, w, h);
        checkAllDayEvent.setBounds(x2, y, w, h);

        y += dy;

        lb4.setBounds(x1, y, w, h);
        tf4.setBounds(x2, y, w, h);

        y += dy;

        lb5.setBounds(x1, y, w, h);
        tf5.setBounds(x2, y, w, h);

        generalPanel.add(lb1);
        generalPanel.add(tf1);
        generalPanel.add(lb2);
        generalPanel.add(tf2);
        generalPanel.add(lb6);
        generalPanel.add(tf6);
        generalPanel.add(lb3);
        generalPanel.add(tf3);
        generalPanel.add(lb7);
        generalPanel.add(tf7);
        generalPanel.add(lb8);
        generalPanel.add(checkAllDayEvent);
        generalPanel.add(lb4);
        generalPanel.add(tf4);
        generalPanel.add(lb5);
        generalPanel.add(tf5);

        scrollGeneralPane = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        scrollGeneralPane.add(generalPanel);

        fieldPanel = new Panel();
        fieldPanel.setSize(100,100);
        cardLayout = new CardLayout();
        fieldPanel.setLayout(cardLayout);
        fieldPanel.add(scrollGeneralPane,"general");

        //
        // Default
        //
        cardLayout.show(fieldPanel, "general");

        setLayout(new BorderLayout());
        add(fieldPanel,BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent evt) {
        //do nothing
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released)
     * on a component.
     * This method adjusts the scrollpanes so that the selected textfield is
     * placed on top of the pane.
     *
     * @param evt the occurred event
     */
    public void mouseClicked(MouseEvent evt) {
        //do nothing
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

    /**
     * Invoked when the checkAllDayEvent changes his state.
     *
     * @param evt the occurred event
     */
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            setAsAllDayEvent(false);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            setAsAllDayEvent(true);
        }

    }

    //-------------------------------------------------------- Protected methods

    /**
     * Sets the fields with the values contained in the specified Calendar object.
     * Used to display the old value of calendar
     *
     * @param calendar the Calendar object which is the source of the values
     */
    protected void setFields(Calendar calendar) {

        this.calendar = calendar;

        String dateStart = null;
        String dateEnd   = null;
        String timeStart = null;
        String timeEnd   = null;

        dateStart = calendar.getEvent().getDtStart().getPropertyValueAsString();
        dateEnd   = calendar.getEvent().getDtEnd().getPropertyValueAsString()  ;

        if (!calendar.getEvent().isAllDay()) {

            //
            // If the date is in UTC date, converts the date in local time
            // and in the format dd/MM/yyyy HH:mm:ss
            // If the date is just in local time but in the UTC without Z 
            // format, converts the date in the format dd/MM/yyyy HH:mm:ss
            //
            try {
                String pattern = TimeUtils.getDateFormat(dateStart);
                if (TimeUtils.PATTERN_UTC.equals(pattern)) {
                    dateStart = FieldsHelper.convertDateFromUTC(dateStart);
                } else if (TimeUtils.PATTERN_UTC_WOZ.equals(pattern)) {
                    dateStart = 
                        TimeUtils.convertDateFromTo(dateStart, 
                                                    TimeUtils.PATTERN_LOCALTIME);
                }
                if (dateStart != null && dateStart.length() == 19) {
                    timeStart = dateStart.substring(11,16);
                    dateStart = dateStart.substring(0,10);
                }

                pattern = TimeUtils.getDateFormat(dateEnd);
                if (TimeUtils.PATTERN_UTC.equals(pattern)) {
                    dateEnd = FieldsHelper.convertDateFromUTC(dateEnd);
                } else if (TimeUtils.PATTERN_UTC_WOZ.equals(pattern)) {
                    dateEnd = 
                        TimeUtils.convertDateFromTo(dateEnd, 
                                                    TimeUtils.PATTERN_LOCALTIME);
                }

                if (dateEnd != null && dateEnd.length() == 19) {
                    timeEnd = dateEnd.substring(11,16);
                    dateEnd = dateEnd.substring(0,10);
                }
            } catch(ParseException e) {
                //ignored but we should handle
            }

            tf6.setText(timeStart != null ? timeStart : "");
            tf6.setEnabled(true);
            tf6.setBackground(java.awt.SystemColor.window);
            tf7.setText(timeEnd != null ? timeEnd : "");
            tf7.setEnabled(true);
            tf7.setBackground(java.awt.SystemColor.window);

            checkAllDayEvent.setState(false);

        } else {
            tf6.setText("");
            tf6.setEnabled(false);
            tf6.setBackground(new Color(230, 230, 230));
            tf7.setText("");
            tf7.setEnabled(false);
            tf7.setBackground(new Color(230, 230, 230));

            try {
                dateStart = FieldsHelper.convertInDayFormatFrom(dateStart);
                dateEnd   = FieldsHelper.convertInDayFormatFrom(dateEnd)  ;
            } catch (ParseException e) {
                //ignored but we should handle
            }

            checkAllDayEvent.setState(true);
        }

        // General
        tf1.setText(calendar.getEvent().getSummary().getPropertyValueAsString());
        tf2.setText(dateStart);
        tf3.setText(dateEnd  );
        tf4.setText(calendar.getEvent().getLocation().getPropertyValueAsString());
        tf5.setText(calendar.getEvent().getDescription().getPropertyValueAsString());
    }
    
    /**
     * Returns a new Calendar object containing the informations set in the textfields.
     * This method also builds the LABELs from the provided addresses, when at least
     * street, city, state, postal code and country are specified.
     *
     * @return the Calendar object containing the informations set in the textfields
     */
    protected Calendar getFields(){

        if (this.calendar == null) {
            calendar = new Calendar(new Event());
        }
        String startDate = FieldsHelper.dateNormalize(tf2.getText());
        String endDate   = FieldsHelper.dateNormalize(tf3.getText());

        if (!checkAllDayEvent.getState()) {
            String startTime = FieldsHelper.timeNormalize(tf6.getText());
            String endTime   = FieldsHelper.timeNormalize(tf7.getText());
            String fullStartDate = startDate + " " + startTime + ":00";
            String fullEndDate   = endDate   + " " + endTime   + ":00";
            startDate = FieldsHelper.convertDateToUTC(fullStartDate);
            endDate   = FieldsHelper.convertDateToUTC(fullEndDate  );
            calendar.getEvent().setAllDay(Boolean.FALSE);
        } else {
            try {
                startDate = FieldsHelper.convertInDayFormatTo(startDate);
                endDate   = FieldsHelper.convertInDayFormatTo(endDate  );
            } catch (ParseException e) {
                //ignored but we should handle
            }
            calendar.getEvent().setAllDay(Boolean.TRUE);
        }

        calendar.getEvent().getSummary    ().setPropertyValue(tf1.getText());
        calendar.getEvent().getDtStart    ().setPropertyValue(startDate    );
        calendar.getEvent().getDtEnd      ().setPropertyValue(endDate      );
        calendar.getEvent().getLocation   ().setPropertyValue(tf4.getText());
        calendar.getEvent().getDescription().setPropertyValue(tf5.getText());

        return calendar;
    }

    protected void setAsAllDayEvent(boolean isAllDayEvent) {

        String dateStart = null;
        String dateEnd   = null;
        String timeStart = null;
        String timeEnd   = null;

        dateStart = calendar.getEvent().getDtStart().getPropertyValueAsString();
        dateEnd   = calendar.getEvent().getDtEnd().getPropertyValueAsString()  ;

        if (isAllDayEvent) {

            tf6.setText("");
            tf6.setEnabled(false);
            tf6.setBackground(new Color(230, 230, 230));
            tf7.setText("");
            tf7.setEnabled(false);
            tf7.setBackground(new Color(230, 230, 230));

            try {
                dateStart = FieldsHelper.convertInDayFormatFrom(dateStart);
                dateEnd   = FieldsHelper.convertInDayFormatFrom(dateEnd  );
            } catch (ParseException e) {
                //ignored but we should handle
            }

            checkAllDayEvent.setState(true);

        } else {

            dateStart = FieldsHelper.convertDateFromUTC(dateStart);
            if (dateStart != null && dateStart.length() > 10) {
                timeStart = dateStart.substring(11,16);
                dateStart = dateStart.substring(0,10);
            }

            dateEnd = FieldsHelper.convertDateFromUTC(dateEnd);
            if (dateEnd != null &&   dateEnd.length() > 10) {
                timeEnd = dateEnd.substring(11,16);
                dateEnd = dateEnd.substring(0,10);
            }

            tf6.setText(timeStart != null ? timeStart : "");
            tf6.setEnabled(true);
            tf6.setBackground(java.awt.SystemColor.window);
            tf7.setText(timeEnd != null ? timeEnd : "");
            tf7.setEnabled(true);
            tf7.setBackground(java.awt.SystemColor.window);

            checkAllDayEvent.setState(false);
        }
    }

    /**
     * Sets all the textfields to an empty string.
     */
    protected void blankFields() {
        tf1.setText("");
        tf2.setText("");
        tf3.setText("");
        tf4.setText("");
        tf5.setText("");
        tf6.setText("");
        tf6.setEnabled(true);
        tf6.setBackground(java.awt.SystemColor.window);
        tf7.setText("");
        tf7.setEnabled(true);
        tf7.setBackground(java.awt.SystemColor.window);
        checkAllDayEvent.setState(false);

        this.calendar = new Calendar(new Event());
    }

	/**
     * Generates the order that the JTextField are cycled.
     */
    protected void setNextFocusableProcedure() {
        tf1.setNextFocusableComponent(tf2);
        tf2.setNextFocusableComponent(tf6);
        tf6.setNextFocusableComponent(tf3);
        tf3.setNextFocusableComponent(tf7);
        tf7.setNextFocusableComponent(tf4);
        tf4.setNextFocusableComponent(tf5);
        tf5.setNextFocusableComponent(tf1);
    }
}
